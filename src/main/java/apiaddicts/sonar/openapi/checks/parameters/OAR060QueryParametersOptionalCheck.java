package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR060QueryParametersOptionalCheck.KEY)
public class OAR060QueryParametersOptionalCheck extends AbstractParameterCheck {

    public static final String KEY = "OAR060";
    private static final String MESSAGE = "OAR060.error";

    @Override
    protected void visitParameterNode(JsonNode node) {

        JsonNode inNode = node.get("in");

        if (inNode != null && "query".equals(inNode.getTokenValue())) {

            JsonNode requiredNode = node.get("required");

            if (requiredNode != null && "true".equals(requiredNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE), requiredNode);
            }
        }
    }
}