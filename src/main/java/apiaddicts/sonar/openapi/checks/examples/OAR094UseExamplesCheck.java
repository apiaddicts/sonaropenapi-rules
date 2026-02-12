package apiaddicts.sonar.openapi.checks.examples;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

import java.util.Set;

@Rule(key = OAR094UseExamplesCheck.KEY)
public class OAR094UseExamplesCheck extends BaseCheck {

    protected JsonNode externalRefNode= null;
    public static final String KEY = "OAR094";
    private static final String MESSAGE = "OAR094.error";

    private static final String EXAMPLE = "example";


    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi31Grammar.ROOT, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType()) || OpenApi31Grammar.ROOT.equals(node.getType())) {
            deepSearchForExample(node);
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        }
    }

    private void deepSearchForExample(JsonNode node) {
        if (node.propertyMap().containsKey(EXAMPLE)) {
            addIssue(KEY, translate(MESSAGE), node.propertyMap().get(EXAMPLE).key());
            return; 
        }
        // Recurse into children
        for (JsonNode child : node.propertyMap().values()) {
            deepSearchForExample(child);
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

    private void visitSchemaNode(JsonNode responseNode) {
        JsonNode schemaNode = responseNode.value().get("schema");
        if (schemaNode.isMissing()) return;

        handleExternalRef(schemaNode, resolvedSchema -> {
            JsonNode props = resolvedSchema.get("properties");
            if (props.isMissing() || !props.isObject()) return;

            props.propertyMap().forEach((key, propertyNode) -> {
                JsonNode exampleNode = propertyNode.get(EXAMPLE);
                if (!exampleNode.isMissing()) {
                    addIssue(KEY, translate(MESSAGE), getTrueNode(exampleNode.key()));
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

    protected JsonNode getTrueNode (JsonNode node){
        return externalRefNode== null ? node : externalRefNode;
    }
}