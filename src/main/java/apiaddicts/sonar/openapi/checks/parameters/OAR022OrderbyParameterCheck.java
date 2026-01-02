package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR022OrderbyParameterCheck.KEY)
public class OAR022OrderbyParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR022";
    private static final String MESSAGE = "OAR022.error";
    private static final String PARAM_NAME = "$orderby";

    public OAR022OrderbyParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }
}
