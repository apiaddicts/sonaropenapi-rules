package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR062PostMethodCheck.KEY)
public class OAR062PostMethodCheck extends AbstractHttpMethodCheck {

    public static final String KEY = "OAR062";
    private static final String MESSAGE = "OAR062.error";
    private static final String MANDATORY_CODES = "200, 201, 202, 204, 206";

    public OAR062PostMethodCheck() {
        super(KEY, MESSAGE, "post", MANDATORY_CODES);
    }
}