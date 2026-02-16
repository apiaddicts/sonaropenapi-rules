package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR064PatchMethodCheck.KEY)
public class OAR064PatchMethodCheck extends AbstractHttpMethodCheck {

    public static final String KEY = "OAR064";
    private static final String MESSAGE = "OAR064.error";
    private static final String MANDATORY_CODES = "200, 202, 204, 206";

    public OAR064PatchMethodCheck() {
        super(KEY, MESSAGE, "patch", MANDATORY_CODES);
    }
}