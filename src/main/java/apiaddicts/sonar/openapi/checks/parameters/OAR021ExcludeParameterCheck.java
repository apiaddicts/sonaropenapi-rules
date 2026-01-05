package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;

@Rule(key = OAR021ExcludeParameterCheck.KEY)
public class OAR021ExcludeParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR021";
    private static final String MESSAGE = "OAR021.error";
    private static final String PARAM_NAME = "$exclude";

    public OAR021ExcludeParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }
}