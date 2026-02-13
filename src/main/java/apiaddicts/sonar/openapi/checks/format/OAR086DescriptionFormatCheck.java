package apiaddicts.sonar.openapi.checks.format;

import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;



import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

@Rule(key = OAR086DescriptionFormatCheck.KEY)
public class OAR086DescriptionFormatCheck extends BaseCheck {

    protected JsonNode externalRefNode= null;
    public static final String KEY = "OAR086";
    private static final String MESSAGE = "OAR086.error";
    private static final String DESCRIPTION = "description";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi31Grammar.ROOT, OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi31Grammar.PATHS, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType()) || OpenApi31Grammar.ROOT.equals(node.getType())) {
            checkInfoDescription(node);
            checkDefinitionsDescription(node);
        }
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())|| OpenApi31Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        }
    }

    protected void visitPathsNode(JsonNode pathsNode) {
        pathsNode.propertyMap().values().forEach(pathNode ->
            pathNode.propertyMap().values().forEach(operation -> {
                checkDescriptionFormat(operation.get(DESCRIPTION));

                operation.get("parameters").elements().forEach(param ->
                    checkDescriptionFormat(param.get(DESCRIPTION)));

                JsonNode responses = operation.get("responses");
                if (!responses.isMissing()) {
                    responses.propertyMap().values().forEach(res ->
                        handleExternalRef(res, resolved -> checkDescriptionFormat(resolved.get(DESCRIPTION)))
                    );
                }
            })
        );
    }

    private void checkInfoDescription(JsonNode rootNode) {
        JsonNode infoNode = rootNode.get("info");
        if (infoNode != null) {
            checkDescriptionFormat(infoNode.get(DESCRIPTION));
        }
    }

    
    private void checkDefinitionsDescription(JsonNode rootNode) {
        Stream.of(rootNode.get("definitions"), rootNode.at("/components/schemas"))
            .filter(node -> !node.isMissing())
            .flatMap(node -> node.propertyMap().values().stream())
            .forEach(schema -> {
                checkDescriptionFormat(schema.get(DESCRIPTION));
                JsonNode props = schema.get("properties");
                if (!props.isMissing()) {
                    props.propertyMap().values().forEach(prop -> checkDescriptionFormat(prop.get(DESCRIPTION)));
                }
            });
    }

    private void visitSchemaNode(JsonNode responseNode) {
        JsonNode schemaNode = responseNode.value().get("schema");

        if (schemaNode.isMissing()) {
            return;
        }

        boolean externalRefManagement = false;
        if (isExternalRef(schemaNode) && externalRefNode == null) {
            externalRefNode = schemaNode;
            externalRefManagement = true;
        }

        schemaNode = resolve(schemaNode);

        Map<String, JsonNode> properties = schemaNode.propertyMap();
        if (!properties.isEmpty()) {
            for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
                String key = entry.getKey();
                JsonNode propertyNode = entry.getValue();

                if (key.contains(DESCRIPTION)) {
                    checkDescriptionFormat(propertyNode);
                }
            }
        }

        if (externalRefManagement) {
            externalRefNode = null;
        }
    }

    private void visitPathNode(JsonNode node) {
        node.properties().stream()
            .filter(prop -> isOperation(prop))
            .map(JsonNode::value)
            .map(operation -> operation.get("responses"))
            .filter(responses -> !responses.isMissing())
            .flatMap(responses -> responses.propertyMap().values().stream())
            .forEach(response -> handleExternalRef(response, resolved -> {
                if (resolved.getType().equals(OpenApi2Grammar.RESPONSE)) {
                    visitSchemaNode(resolved);
                } else if (resolved.getType().equals(OpenApi3Grammar.RESPONSE)) {
                    resolved.at("/content").propertyMap().forEach((mediaType, mediaTypeNode) -> {
                        if (mediaType.toLowerCase().contains("json")) visitSchemaNode(mediaTypeNode);
                    });
                }
            }));
    }

    private void checkDescriptionFormat(JsonNode descriptionNode) {
        if (descriptionNode == null || descriptionNode.isMissing()) {
            return;
        }

        String description = descriptionNode.getTokenValue();
        description = description == null ? "" : description.trim();

        if (description.isEmpty()) {
            addIssue(KEY, translate(MESSAGE), getTrueNode(descriptionNode));
            return;
        }

        if (!Character.isUpperCase(description.charAt(0)) || !description.endsWith(".")) {
            addIssue(KEY, translate(MESSAGE), getTrueNode(descriptionNode));
        }
    }

    private void handleExternalRef(JsonNode node, java.util.function.Consumer<JsonNode> action) {
        if (node == null || node.isMissing()) return;
        boolean setExternal = false;
        if (isExternalRef(node) && externalRefNode == null) {
            externalRefNode = node;
            setExternal = true;
        }
        try {
            action.accept(resolve(node));
        } finally {
            if (setExternal) externalRefNode = null;
        }
    }

    protected JsonNode getTrueNode (JsonNode node){
        return externalRefNode== null ? node : externalRefNode;
    }

}