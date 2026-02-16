package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR063PutMethodCheck.KEY)
public class OAR063PutMethodCheck extends AbstractHttpMethodCheck {

    public static final String KEY = "OAR063";
    private static final String MESSAGE = "OAR063.error";
    private static final String MANDATORY_CODES = "200, 202, 204, 206";

    public OAR063PutMethodCheck() {
        super(KEY, MESSAGE, "put", MANDATORY_CODES);
    }
}