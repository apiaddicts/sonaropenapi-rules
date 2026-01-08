package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR020ExpandParameterCheck.KEY)
public class OAR020ExpandParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR020";
    private static final String MESSAGE = "OAR020.error";
    private static final String PARAM_NAME = "$expand";

    public OAR020ExpandParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }

}