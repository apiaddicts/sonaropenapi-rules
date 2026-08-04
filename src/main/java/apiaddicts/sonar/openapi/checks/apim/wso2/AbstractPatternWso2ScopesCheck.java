package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractPatternWso2ScopesCheck extends AbstractWso2ScopesCheck {

    protected final String ruleKey;
    protected final String messageKey;
    protected final String fieldName;
    protected final String defaultPatternValue;

    protected Pattern pattern;

    protected AbstractPatternWso2ScopesCheck(String key, String message, String fieldName, String defaultPatternValue) {
        this.ruleKey = key;
        this.messageKey = message;
        this.fieldName = fieldName;
        this.defaultPatternValue = defaultPatternValue;
    }

    protected abstract String getPatternStr();

    @Override
    protected void visitFile(JsonNode root) {
        String patternStr = getPatternStr();
        pattern = Pattern.compile(patternStr != null ? patternStr : defaultPatternValue);
    }

    @Override
    protected void visitScope(JsonNode scope) {
        JsonNode fieldNode = scope.propertyMap().get(fieldName);
        if (fieldNode == null || fieldNode.isNull() || fieldNode.isMissing())
            return;

        String patternStr = getPatternStr() != null ? getPatternStr() : defaultPatternValue;
        List<JsonNode> elements = fieldNode.elements();
        if (!elements.isEmpty()) {
            for (JsonNode element : elements) {
                String roleText = element.getTokenValue();
                if (roleText != null && !pattern.matcher(roleText).matches()) {
                    addIssue(ruleKey, translate(messageKey, patternStr), element);
                }
            }
        } else {
            String fieldText = fieldNode.getTokenValue();
            if (fieldText != null && !pattern.matcher(fieldText).matches()) {
                addIssue(ruleKey, translate(messageKey, patternStr), fieldNode);
            }
        }
    }
}