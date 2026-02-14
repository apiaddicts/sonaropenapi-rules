package apiaddicts.sonar.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiaddicts.sonar.openapi.utils.ExternalRefHandler;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

import java.util.Map;
import java.util.Set;

@Rule(key = OAR068PascalCaseNamingConventionCheck.KEY)
public class OAR068PascalCaseNamingConventionCheck extends AbstractNamingConventionCheck {

    public static final String KEY = "OAR068";
    private static final String MESSAGE = "OAR068.error";
    private static final String NAMING_CONVENTION = PASCAL_CASE;
    private static final String SCHEMA = "schema";
    private static final String PROPERTIES = "properties";

    private final ExternalRefHandler handleExternalRef = new ExternalRefHandler();

    public OAR068PascalCaseNamingConventionCheck() {
        super(KEY, MESSAGE, NAMING_CONVENTION);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi31Grammar.PATHS, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        }
    }

    private void visitPathsNode(JsonNode pathsNode) {
        pathsNode.propertyMap().values().forEach(pathNode ->
            pathNode.propertyMap().values().forEach(operationNode -> {
                JsonNode parameters = operationNode.get("parameters");
                if (!parameters.isMissing()) {
                    parameters.elements().stream()
                            .filter(p -> "body".equals(p.get("in").getTokenValue()))
                            .forEach(this::visitParameterNode);
                }
                handleExternalRef.resolve(operationNode.get("requestBody"), this::visitRequestBodyNode);
                handleExternalRef.resolve(operationNode.get("responses"), this::visitResponsesNode);
            })
        );
    }

    private void visitParameterNode(JsonNode parameterNode) {
        JsonNode inNode = parameterNode.get("in");
        if (!inNode.isMissing() && "body".equals(inNode.getTokenValue())) {
            JsonNode schemaNode = parameterNode.get(SCHEMA);
            if (!schemaNode.isMissing()) {
                visitSchemaNode(schemaNode);
            }
        } else {
            JsonNode nameNode = parameterNode.get("name");
            if (!nameNode.isMissing()) {
                String name = nameNode.getTokenValue();
                validateNamingConvention(name, nameNode);
            }
        }
    }

    private void visitRequestBodyNode(JsonNode requestBodyNode) {
        JsonNode contentNode = requestBodyNode.get("content");
        if (!contentNode.isMissing()) {
            for (JsonNode mediaTypeNode : contentNode.propertyMap().values()) {
                JsonNode schemaNode = mediaTypeNode.get(SCHEMA);
                if (!schemaNode.isMissing()) {
                    visitSchemaNode(schemaNode);
                }
            }
        }
    }

    private void visitResponsesNode(JsonNode responsesNode) {
        for (JsonNode responseNode : responsesNode.propertyMap().values()) {
            JsonNode contentNode = responseNode.get("content");
            if (!contentNode.isMissing()) {
                for (JsonNode mediaTypeNode : contentNode.propertyMap().values()) {
                    JsonNode schemaNode = mediaTypeNode.get(SCHEMA);
                    if (!schemaNode.isMissing()) {
                        visitSchemaNode(schemaNode);
                    }
                }
            } else {
                JsonNode schemaNode = responseNode.get(SCHEMA);
                if (!schemaNode.isMissing()) {
                    visitSchemaNode(schemaNode);
                }
            }
        }
    }

    private void visitSchemaNode(JsonNode schemaNode) {
        Map<String, JsonNode> properties = schemaNode.propertyMap();
        if (properties.containsKey(PROPERTIES)) {
            Map<String, JsonNode> schemaProperties = schemaNode.get(PROPERTIES).propertyMap();
            for (JsonNode property : schemaProperties.values()) {
                JsonNode nameNode = property.key();
                String name = nameNode.getTokenValue();
                validateNamingConvention(name, nameNode);
            }
        }
    }

    private void visitPathNode(JsonNode node) {
        node.properties().stream()
            .filter(prop -> isOperation(prop))
            .map(JsonNode::value)
            .map(operation -> operation.get("responses"))
            .filter(responses -> !responses.isMissing())
            .flatMap(responses -> responses.propertyMap().values().stream())
            .forEach(response -> handleExternalRef.resolve(response, resolved -> {
                if (resolved.getType().equals(OpenApi2Grammar.RESPONSE)) {
                    visitSchemaNode2(resolved);
                } else if (resolved.getType().equals(OpenApi3Grammar.RESPONSE)) {
                    JsonNode content = resolved.at("/content");
                    if (!content.isMissing()) {
                        content.propertyMap().forEach((mediaType, mediaTypeNode) -> {
                            if (mediaType.toLowerCase().contains("json")) {
                                visitSchemaNode2(mediaTypeNode);
                            }
                        });
                    }
                }
            }));
    }

    private void visitSchemaNode2(JsonNode responseOrMediaTypeNode) {
        JsonNode schemaNode = responseOrMediaTypeNode.get(SCHEMA);
        if (schemaNode.isMissing()) {
            schemaNode = responseOrMediaTypeNode.value().get(SCHEMA);
        }

        handleExternalRef.resolve(schemaNode, schemaResolved -> {
            JsonNode propsNode = schemaResolved.get(PROPERTIES);
            if (!propsNode.isMissing() && propsNode.isObject()) {
                propsNode.propertyMap().forEach((name, property) ->
                    validateNamingConvention(name, handleExternalRef.getTrueNode(property.key()))
                );
            }
        });
    }
}