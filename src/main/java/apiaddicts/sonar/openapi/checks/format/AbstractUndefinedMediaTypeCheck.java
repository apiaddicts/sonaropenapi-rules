package apiaddicts.sonar.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import apiaddicts.sonar.openapi.utils.MediaTypeUtils;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.Map;
import java.util.Set;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

public abstract class AbstractUndefinedMediaTypeCheck extends BaseCheck {

    private static final String MESSAGE = "generic.section";

    private String key;
    private String section;
    protected JsonNode externalRefNode = null;
    private boolean globalDefinesMediaTypes = false;

    public AbstractUndefinedMediaTypeCheck(String key, String section) {
        this.key = key;
        this.section = section;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION, OpenApi3Grammar.RESPONSES, OpenApi31Grammar.RESPONSES);
    }

    @Override
    protected void visitFile(JsonNode root) {
        globalDefinesMediaTypes = (root.getType() instanceof OpenApi2Grammar) ? definesMimeTypesV2(root) : false;
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() instanceof OpenApi2Grammar) {
            visitV2Node(node);
        } else {
            visitV3Node(node);
        }
    }

    private void visitV2Node(JsonNode node) {
        if (node.getType() == OpenApi2Grammar.OPERATION) {
            String operation = node.key().getTokenValue().toLowerCase();

            if ((operation.equals("post") || operation.equals("put") || operation.equals("patch"))
                && !globalDefinesMediaTypes
                && !definesMimeTypesV2(node)) {

                addIssue(key, translate(MESSAGE, section), node.key());
            }
        }
    }

    private void visitV3Node(JsonNode node) {
        if (node.getType() == OpenApi3Grammar.OPERATION && section.equals("consumes")) {
            handleConsumesOperation(node);
            return;
        }

        if (node.getType() == OpenApi3Grammar.RESPONSES && section.equals("produces")) {
            MediaTypeUtils.handleProducesResponses(node, externalRefNode, this::visitContentNode);
        }
    }

    private void handleConsumesOperation(JsonNode node) {
        String operation = node.key().getTokenValue().toLowerCase();

        if (operation.equals("post") || operation.equals("put") || operation.equals("patch")) {
            visitContentNode(node);
        }
    }

    private void visitContentNode(JsonNode node) {
        JsonNode contentNode;

        if (section.equals("consumes")) {
            JsonNode requestBodyNode = node.at("/requestBody");
            if (requestBodyNode.isMissing() || requestBodyNode.isNull()) {
                addIssue(key, translate(MESSAGE, "requestBody"), node.key());
                return;
            }
            contentNode = MediaTypeUtils.getContentNode(node, section);
        } else {
            contentNode = MediaTypeUtils.getContentNode(node, section);
        }

        if (!globalDefinesMediaTypes && !definesMimeTypesV3(contentNode)) {
            addIssue(key, translate(MESSAGE, "content"), node.key());
        }
    }

    private boolean definesMimeTypesV2(JsonNode node) {
        JsonNode consumes = node.get(section);
        if (consumes.isMissing() || consumes.isNull()) return false;
        return !consumes.elements().isEmpty();
    }

    private boolean definesMimeTypesV3(JsonNode content) {
        Map<String, JsonNode> mimeTypesList = content.propertyMap();
        if (content.isMissing() || content.isNull()) return false;
        return !mimeTypesList.isEmpty();
    }
}
