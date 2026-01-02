package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR025LimitParameterCheck.KEY)
public class OAR025LimitParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR025";
    private static final String MESSAGE = "OAR025.error";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = "$limit"
    )
    protected String parameterName = "$limit";

    @Override
    protected String getDefaultParameterName() {
        return parameterName;
    }

    @Override
    public void visitNode(JsonNode node) {
        if ("get".equals(node.key().getTokenValue())) {

            String path = getPath(node);

            boolean hasParameter = hasParameterInNode(node);

            if (shouldIncludePath(path) && !hasParameter && !isSingleResourcePath(path)) {
                addIssue(KEY, translate(MESSAGE, parameterName), node.key());
            }
        }
    }

    private boolean isSingleResourcePath(String path) {
        return path.matches(".*/\\{[^/]+\\}$");
    }
}