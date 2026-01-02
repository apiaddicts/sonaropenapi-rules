package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR020ExpandParameterCheck.KEY)
public class OAR020ExpandParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR020";
    private static final String MESSAGE = "OAR020.error";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = "$expand"
    )
    protected String parameterName = "$expand";

    @Override
    protected String getDefaultParameterName() {
        return parameterName;
    }

    @Override
    public void visitNode(JsonNode node) {
        if ("get".equals(node.key().getTokenValue())) {

            String path = getPath(node);

            boolean hasParameter = hasParameterInNode(node);

            if (shouldIncludePath(path) && !hasParameter) {
                addIssue(KEY, translate(MESSAGE, parameterName), node.key());
            }
        }
    }
}