package apiaddicts.sonar.openapi.checks.parameters;

public abstract class AbstractCollectionQueryParameterCheck extends AbstractQueryParameterCheck {

    protected AbstractCollectionQueryParameterCheck(
        String ruleKey,
        String messageKey,
        String defaultParameterName,
        boolean applyToParameterizedPaths
    ) {
        super(ruleKey, messageKey, defaultParameterName, applyToParameterizedPaths);
    }
}
