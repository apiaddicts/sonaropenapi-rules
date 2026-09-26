package apiaddicts.sonar.openapi.checks.apim.wso2;

import com.sonar.sslr.api.AstNode;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.utils.JsonNodeUtils;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR005UndefinedWso2ScopeUseCheck.KEY)
public class OAR005UndefinedWso2ScopeUseCheck
        extends AbstractWso2OperationCheck {

    public static final String KEY = "OAR005";
    private static final String MESSAGE = "OAR005.error";

    private Set<String> definedScopes;

    @Override
    protected void visitFile(JsonNode root) {
        definedScopes = getScopes(root);
    }

    private Set<String> getScopes(JsonNode root) {

        JsonNode scopes = JsonNodeUtils
                .getWso2ApimNode(root)
                .get(JsonNodeUtils.WSO2_SCOPES);

        return JsonNodeUtils.getWso2Scopes(scopes).stream()
                .map(node -> node.get("name"))
                .filter(node -> !node.isMissing() && !JsonNodeUtils.isNullScalar(node))
                .map(AstNode::getTokenValue)
                .collect(Collectors.toSet());
    }

    @Override
    protected void visitOperationNode(JsonNode node) {

        JsonNode scopeNode = node.get("x-scope");

        if (scopeNode.isMissing()) return;

        if (JsonNodeUtils.isNullScalar(scopeNode) || !definedScopes.contains(scopeNode.getTokenValue())) {
            addIssue(KEY, translate(MESSAGE), scopeNode);
        }
    }
}
