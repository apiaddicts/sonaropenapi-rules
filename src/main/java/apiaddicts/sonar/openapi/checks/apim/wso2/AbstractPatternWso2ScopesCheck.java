package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.regex.Pattern;

public abstract class AbstractPatternWso2ScopesCheck extends AbstractWso2ScopesCheck {

    protected final String ruleKey;
    protected final String messageKey;
    protected final String fieldName;
    protected final String defaultPatternValue;

    protected Pattern pattern;
    private String patternStr;

    protected AbstractPatternWso2ScopesCheck(String key, String message, String fieldName, String defaultPatternValue) {
        this.ruleKey = key;
        this.messageKey = message;
        this.fieldName = fieldName;
        this.defaultPatternValue = defaultPatternValue;
    }

    @Override
    protected void visitFile(JsonNode root) {
        pattern = Pattern.compile(patternStr != null ? patternStr : defaultPatternValue);
    }

    @Override
    protected void visitScope(JsonNode scope) {
        JsonNode fieldNode = scope.propertyMap().get(fieldName);
        if (fieldNode == null || fieldNode.isNull() || fieldNode.isMissing()) return;

        String fieldText = fieldNode.getTokenValue();
        boolean notValid = !pattern.matcher(fieldText).matches();

        if (notValid) addIssue(ruleKey, translate(messageKey), fieldNode.value());
    }
}