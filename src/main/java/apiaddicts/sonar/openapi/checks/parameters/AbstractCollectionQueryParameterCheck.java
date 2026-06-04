package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

public abstract class AbstractCollectionQueryParameterCheck extends AbstractQueryParameterCheck {

    protected AbstractCollectionQueryParameterCheck(
        String ruleKey,
        String messageKey,
        String parameterName,
        boolean applyToParameterizedPaths
    ) {
        super(ruleKey, messageKey, parameterName, applyToParameterizedPaths);
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
