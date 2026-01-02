package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR023TotalParameterCheck.KEY)
public class OAR023TotalParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR023";
    private static final String MESSAGE = "OAR023.error";
    private static final String PARAM_NAME = "$total";

    public OAR023TotalParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            true
        );
    }
}