package apiaddicts.sonar.openapi.checks.examples;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import apiaddicts.sonar.openapi.utils.ExternalRefHandler;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.getType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isArrayType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isObjectType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isOperation;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.Set;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR031ExamplesCheck.KEY)
public class OAR031ExamplesCheck extends BaseCheck {
    public static final String KEY = "OAR031";

    private static final String EXAMPLE = "example";
    private static final String EXAMPLES = "examples";
    private static final String SCHEMA = "schema";
    private static final String PROPERTIES = "properties";
    private static final String ITEMS = "items";

    private static final String ERROR_RESPONSE = "OAR031.error-response";

    private final ExternalRefHandler handleExternalRef = new ExternalRefHandler();

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
            OpenApi2Grammar.SCHEMA, OpenApi2Grammar.RESPONSES, OpenApi2Grammar.PARAMETER,
            OpenApi3Grammar.SCHEMA, OpenApi3Grammar.RESPONSES, OpenApi3Grammar.PARAMETER,
            OpenApi31Grammar.SCHEMA, OpenApi31Grammar.RESPONSES, OpenApi31Grammar.PARAMETER,
            OpenApi3Grammar.REQUEST_BODY, OpenApi31Grammar.REQUEST_BODY, 
            OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        AstNodeType type = node.getType();
        if (OpenApi2Grammar.PATH.equals(type) || OpenApi3Grammar.PATH.equals(type) || OpenApi31Grammar.PATH.equals(type)) {
            visitPathNode(node);
        } else if (OpenApi2Grammar.PARAMETER.equals(type) || OpenApi3Grammar.PARAMETER.equals(type) || OpenApi31Grammar.PARAMETER.equals(type)) {
            visitParameterNode(node);
        } else if (type.equals(OpenApi2Grammar.RESPONSES) || type.equals(OpenApi2Grammar.SCHEMA)) {
            visitV2Node(node);
        } else {
            visitV3Node(node);
        }
    }

    private void visitParameterNode(JsonNode node) {
        handleExternalRef.resolve(node, resolved -> {
            if (OpenApi2Grammar.PARAMETER.equals(resolved.getType())) {
                JsonNode inNode = resolved.get("in");
                if (!inNode.isMissing() && !"body".equals(inNode.getTokenValue())) {
                    return;
                }
            }

            JsonNode schema = resolved.get(SCHEMA);

            boolean hasExample = !resolved.get(EXAMPLE).isMissing()
                    || !resolved.get(EXAMPLES).isMissing()
                    || (!schema.isMissing() && isSchemaCovered(schema));

            if (!hasExample) {
                addIssue(KEY, translate("OAR031.error-parameter"), handleExternalRef.getTrueNode(node));
            }
        });
    }

    private void visitV2Node(JsonNode node) {
        AstNodeType type = node.getType();
        if (OpenApi2Grammar.RESPONSES.equals(type)) {
            processResponses(node, this::visitResponseV2Node);
        } else if (OpenApi2Grammar.SCHEMA.equals(type)) {
            visitSchemaNode(node);
        }
    }

    private void visitResponseV2Node(JsonNode node) {
        handleExternalRef.resolve(node, resolved -> {
            JsonNode schemaNode = resolved.get(SCHEMA);
            boolean hasExample = !resolved.get(EXAMPLES).isMissing()
                    || (!schemaNode.isMissing() && isSchemaCovered(schemaNode));

            if (!hasExample) {
                addIssue(KEY, translate(ERROR_RESPONSE), handleExternalRef.getTrueNode(node.key()));
            }
        });
    }

    private void visitV3Node(JsonNode node) {
        AstNodeType type = node.getType();
        if (OpenApi3Grammar.RESPONSES.equals(type)) {
            processResponses(node, this::visitRequestBodyOrResponseV3Node);
        } else if (OpenApi3Grammar.SCHEMA.equals(type)) {
            visitSchemaNode(node);
        } else if (OpenApi3Grammar.REQUEST_BODY.equals(type)) {
            visitRequestBodyOrResponseV3Node(node);
        }
    }

    private void processResponses(JsonNode node, java.util.function.Consumer<JsonNode> visitor) {
        for (JsonNode responseNode : node.properties()) {
            if ("204".equals(responseNode.key().getTokenValue())) continue;
            handleExternalRef.resolve(responseNode, visitor);
        }
    }

    private void visitRequestBodyOrResponseV3Node(JsonNode node) {
        JsonNode content = node.at("/content");

        if (content.isMissing()) {
            String errorKey = node.getType().equals(OpenApi3Grammar.REQUEST_BODY) ? "OAR031.error-request" : ERROR_RESPONSE;
            addIssue(KEY, translate(errorKey), handleExternalRef.getTrueNode(node.key()));
            return;
        }

        for (JsonNode mediaTypeNode : content.propertyMap().values()) {
            JsonNode schemaNode = mediaTypeNode.get(SCHEMA);
            boolean hasExplicitExample = !mediaTypeNode.get(EXAMPLES).isMissing()
                    || !mediaTypeNode.get(EXAMPLE).isMissing();

            if (!hasExplicitExample && !isSchemaCovered(schemaNode)) {
                String errorKey = node.getType().equals(OpenApi3Grammar.REQUEST_BODY) ? "OAR031.error-request" : ERROR_RESPONSE;
                addIssue(KEY, translate(errorKey), handleExternalRef.getTrueNode(node.key()));
            }
        }
    }

    private boolean isSchemaCovered(JsonNode schemaNode) {
        if (schemaNode.isMissing()) return false;

        return handleExternalRef.resolve(schemaNode, resolved -> {
            if (!resolved.get(EXAMPLE).isMissing() || !resolved.get(EXAMPLES).isMissing()) {
                return true;
            }

            JsonNode props = resolved.get(PROPERTIES);
            if (!props.isMissing() && props.isObject()) {
                return props.propertyMap().values().stream().anyMatch(this::isSchemaCovered);
            }

            JsonNode items = resolved.get(ITEMS);
            if (!items.isMissing()) {
                return isSchemaCovered(items);
            }

            return false;
        });
    }

    private void visitSchemaNode(JsonNode node) {
        JsonNode parentNode = (JsonNode) node.getParent().getParent();

        if (parentNode.getType().equals(OpenApi3Grammar.PARAMETER)) {
            return;
        }

        if (parentNode.getType().equals(OpenApi3Grammar.SCHEMA_PROPERTIES)
                || parentNode.getType().toString().equals("BLOCK_MAPPING") 
                || parentNode.getType().toString().equals("FLOW_MAPPING")) {

            JsonNode schemaParent = (JsonNode) parentNode.getParent().getParent();
            if (schemaParent != null && !schemaParent.get("allOf").isMissing()) {
                return;
            }

            JsonNode type = getType(node);
            if (!isObjectType(type) && !type.isMissing() && !isArrayType(type) && node.get(EXAMPLE).isMissing()) {
                addIssue(KEY, translate("OAR031.error-property"), node.key());
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
                                visitSchemaNode(mediaTypeNode);
                            }
                        });
                    }
                }
            }));
    }

    private void visitSchemaNode2(JsonNode responseNode) {
        JsonNode schemaNode = responseNode.value().get(SCHEMA);
        if (schemaNode.isMissing()) return;

        handleExternalRef.resolve(schemaNode, resolvedSchema -> {
            JsonNode props = resolvedSchema.get(PROPERTIES);
            if (props.isMissing() || !props.isObject()) return;

            props.propertyMap().forEach((key, propertyNode) -> {
                if (propertyNode.get(EXAMPLE).isMissing()) {
                    addIssue(KEY, translate("OAR031.error-property"), handleExternalRef.getTrueNode(propertyNode.key()));
                }
            });
        });
    }
}