package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR024StartParameterCheck.KEY)
public class OAR024StartParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR024";
    private static final String MESSAGE = "OAR024.error";
    private static final String PARAM_NAME = "$start";

    public OAR024StartParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            true
        );
    }
}