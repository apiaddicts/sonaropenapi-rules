package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR024StartParameterCheck.KEY)
public class OAR024StartParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR024";
    private static final String MESSAGE = "OAR024.error";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = "$start"
    )
    private String parameterName = "$start";

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