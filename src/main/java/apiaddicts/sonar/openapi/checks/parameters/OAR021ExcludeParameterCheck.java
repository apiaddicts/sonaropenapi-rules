package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR021ExcludeParameterCheck.KEY)
public class OAR021ExcludeParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR021";
    private static final String MESSAGE = "OAR021.error";
    private static final String PARAM_NAME = "$exclude";

    public OAR021ExcludeParameterCheck() {
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