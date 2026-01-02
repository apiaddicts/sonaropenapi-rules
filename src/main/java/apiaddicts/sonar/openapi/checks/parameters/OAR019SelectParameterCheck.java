package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR019SelectParameterCheck.KEY)
public class OAR019SelectParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR019";
    private static final String MESSAGE = "OAR019.error";
    private static final String PARAM_NAME = "$select";

    public OAR019SelectParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }

}