package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR041UndefinedAuthTypeForWso2ScopeCheck.KEY)
public class OAR041UndefinedAuthTypeForWso2ScopeCheck
        extends AbstractWso2OperationCheck {

    public static final String KEY = "OAR041";
    private static final String MESSAGE = "OAR041.error";

    @Override
    protected void visitOperationNode(JsonNode node) {

        JsonNode scopeNode = node.get("x-scope");

        if (scopeNode.isMissing()) return;

        JsonNode authTypeNode = node.get("x-auth-type");

        if (authTypeNode.isMissing()) {
            addIssue(KEY, translate(MESSAGE), scopeNode.key());
        }
    }
}