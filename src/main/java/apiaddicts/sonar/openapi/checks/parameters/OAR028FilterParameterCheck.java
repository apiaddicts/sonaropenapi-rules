package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR028FilterParameterCheck.KEY)
public class OAR028FilterParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR028";
    private static final String MESSAGE = "OAR028.error";
    private static final String DEFAULT_PARAM_NAME = "$filter";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the query parameter to be checked",
        defaultValue = DEFAULT_PARAM_NAME
    )
    private String filterParamName = DEFAULT_PARAM_NAME;

    public OAR028FilterParameterCheck() {
        super(KEY, MESSAGE, DEFAULT_PARAM_NAME, false);
    }

    @Override
    protected void visitFile(JsonNode root) {
        this.parameterName = filterParamName;
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (!"get".equals(node.key().getTokenValue())) return;

        String path = getPath(node);

        if (endsWithPathParam(path)) return;
        if (path.contains("/me/") || path.endsWith("/me")) return;
        if (path.contains("status") || path.contains("health") || path.contains("ping")) return;

        if (!hasParameterInNode(node)) {
            addIssue(ruleKey, translate(messageKey, parameterName), node.key());
        }
    }
}
