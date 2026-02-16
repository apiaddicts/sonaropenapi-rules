package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR061GetMethodCheck.KEY)
public class OAR061GetMethodCheck extends AbstractHttpMethodCheck {

    public static final String KEY = "OAR061";
    private static final String MESSAGE = "OAR061.error";
    private static final String MANDATORY_CODES = "200, 202, 206";

    public OAR061GetMethodCheck() {
        super(KEY, MESSAGE, "get", MANDATORY_CODES);
    }
}