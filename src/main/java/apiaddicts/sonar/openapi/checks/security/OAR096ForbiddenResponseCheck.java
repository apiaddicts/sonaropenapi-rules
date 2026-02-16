package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;

@Rule(key = OAR096ForbiddenResponseCheck.KEY)
public class OAR096ForbiddenResponseCheck extends AbstractSecurityResponseCheck {

    public static final String KEY = "OAR096";
    private static final String MESSAGE = "OAR096.error";
    private static final String RESPONSE_CODES_STR = "403";

    public OAR096ForbiddenResponseCheck() {
        super(KEY, MESSAGE, RESPONSE_CODES_STR);
    }
}