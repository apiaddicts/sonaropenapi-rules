package apiaddicts.sonar.openapi.checks.examples;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.getType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isArrayType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isExternalRef;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isObjectType;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isOperation;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.resolve;
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

    protected JsonNode externalRefNode = null;
    public static final String KEY = "OAR031";

    private static final String EXAMPLE = "example";
    private static final String EXAMPLES = "examples";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi2Grammar.RESPONSES, OpenApi3Grammar.SCHEMA, OpenApi3Grammar.RESPONSES, OpenApi31Grammar.SCHEMA, OpenApi31Grammar.RESPONSES, OpenApi3Grammar.REQUEST_BODY, OpenApi31Grammar.REQUEST_BODY, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        } else if (node.getType().equals(OpenApi2Grammar.RESPONSES) || node.getType().equals(OpenApi2Grammar.SCHEMA)) {
            visitV2Node(node);
        } else {
            visitV3Node(node);
        }
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
        if (node.get(EXAMPLES).isMissing()) {
            addIssue(KEY, translate("OAR031.error-response"), node.key());
        }
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

            boolean setExternal = false;
            if (isExternalRef(responseNode) && externalRefNode == null) {
                externalRefNode = responseNode;
                setExternal = true;
            }

            try {
                visitor.accept(resolve(responseNode));
            } finally {
                if (setExternal) externalRefNode = null;
            }
        }
    }

    private void visitRequestBodyOrResponseV3Node(JsonNode node) {
        JsonNode content = node.at("/content");
        if (content.isMissing()) {
            return;
        }
        for (JsonNode mediaTypeNode : content.propertyMap().values()) {
            AstNodeType type = node.getType();
            JsonNode schemaNode = mediaTypeNode.get("schema");
            boolean hasSchemaExample = false;
            if (schemaNode.getType().equals(OpenApi3Grammar.SCHEMA) && !schemaNode.get(EXAMPLE).isMissing()) {
                hasSchemaExample = true;
            }
            if (!hasSchemaExample && mediaTypeNode.get(EXAMPLES).isMissing() && mediaTypeNode.get(EXAMPLE).isMissing()) {
                if (type.equals(OpenApi3Grammar.REQUEST_BODY)) {
                    addIssue(KEY, translate("OAR031.error-request"), getTrueNode(node.key()));
                } else {
                    addIssue(KEY, translate("OAR031.error-response"), getTrueNode(node.key()));
                }
            }
        }
    }

    private void visitSchemaNode(JsonNode node) {
        JsonNode parentNode = (JsonNode) node.getParent().getParent();

        if (parentNode.getType().equals(OpenApi3Grammar.PARAMETER)) {
            if (node.get(EXAMPLE).isMissing() && parentNode.get(EXAMPLE).isMissing() && parentNode.get(EXAMPLES).isMissing()) {
                addIssue(KEY, translate("OAR031.error-parameter"), parentNode);
            }
        } else if (parentNode.getType().equals(OpenApi3Grammar.SCHEMA_PROPERTIES)
                || parentNode.getType().toString().equals("BLOCK_MAPPING") || parentNode.getType().toString().equals("FLOW_MAPPING")) {
            JsonNode schemaParent = (JsonNode) parentNode.getParent().getParent();
            if (schemaParent != null && !schemaParent.get("allOf").isMissing()) {
                return;
            }

            JsonNode type = getType(node);
            if (!isObjectType(type) && !type.isMissing() && !isArrayType(type)) {
                if (node.get(EXAMPLE).isMissing()) {
                    addIssue(KEY, translate("OAR031.error-property"), node.key());
                }
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
            .forEach(response -> handleExternalRef(response, resolved -> {
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
        JsonNode schemaNode = responseNode.value().get("schema");
        if (schemaNode.isMissing()) return;

        handleExternalRef(schemaNode, resolvedSchema -> {
            JsonNode props = resolvedSchema.get("properties");
            if (props.isMissing() || !props.isObject()) return;

            props.propertyMap().forEach((key, propertyNode) -> {
                if (propertyNode.get(EXAMPLE).isMissing()) {
                    addIssue(KEY, translate("OAR031.error-property"), getTrueNode(propertyNode.key()));
                }
            });
        });
    }

    private void handleExternalRef(JsonNode node, java.util.function.Consumer<JsonNode> action) {
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

    protected JsonNode getTrueNode(JsonNode node) {
        return externalRefNode == null ? node : externalRefNode;
    }
}
