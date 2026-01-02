package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR022OrderbyParameterCheck.KEY)
public class OAR022OrderbyParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR022";
    private static final String MESSAGE = "OAR022.error";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = "$orderby"
    )
    protected String parameterName = "$orderby";

    @Override
    protected String getDefaultParameterName() {
        return parameterName;
    }

    @Override
    public void visitNode(JsonNode node) {
        if ("get".equals(node.key().getTokenValue())) {

            String path = getPath(node);
            if (endsWithPathParam(path)) return;

            boolean hasParameter = hasParameterInNode(node);

            if (shouldIncludePath(path) && !hasParameter) {
                addIssue(KEY, translate(MESSAGE, parameterName), node.key());
            }
        }
    }
}