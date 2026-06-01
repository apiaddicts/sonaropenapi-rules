package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR020ExpandParameterCheck.KEY)
public class OAR020ExpandParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR020";
    private static final String MESSAGE = "OAR020.error";
    private static final String PARAM_NAME = "$expand";

    public OAR020ExpandParameterCheck() {
        super(KEY, MESSAGE, PARAM_NAME, false);
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