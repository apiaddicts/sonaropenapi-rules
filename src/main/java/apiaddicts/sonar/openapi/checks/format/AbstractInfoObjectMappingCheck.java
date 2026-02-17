package apiaddicts.sonar.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Set;

public abstract class AbstractInfoObjectMappingCheck extends BaseCheck {

    private final String ruleKey;
    private final String messageKey;
    private final String targetProperty;

    protected AbstractInfoObjectMappingCheck(
            String ruleKey,
            String messageKey,
            String targetProperty
    ) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.targetProperty = targetProperty;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.INFO,
                OpenApi3Grammar.INFO,
                OpenApi31Grammar.INFO
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        validateInfo(node);
    }

    private void validateInfo(JsonNode infoNode) {
        if (infoNode == null || infoNode.isMissing()) return;
        List<JsonNode> children = infoNode.getJsonChildren();

        for (JsonNode child : children) {
            String property = child.key().getTokenValue();
            if (targetProperty.equals(property)) {
                validateMapping(child);
                break;
            }
        }
    }

    private void validateMapping(JsonNode mappingNode) {
        if (mappingNode == null || mappingNode.isMissing()) return;
        List<JsonNode> children = mappingNode.getJsonChildren();
        boolean isEmpty = true;

        for (JsonNode child : children) {
            String repr = child.toString();
            if (repr.contains("BLOCK_MAPPING")
                    || repr.contains("FLOW_MAPPING")) {
                isEmpty = false;
                break;
            }
        }

        if (isEmpty) {
            addIssue(ruleKey, translate(messageKey), mappingNode.key());
        }
    }
}