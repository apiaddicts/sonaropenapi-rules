package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR025LimitParameterCheck.KEY)
public class OAR025LimitParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR025";
    private static final String MESSAGE = "OAR025.error";
    private static final String PARAM_NAME = "$limit";

    public OAR025LimitParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }
}