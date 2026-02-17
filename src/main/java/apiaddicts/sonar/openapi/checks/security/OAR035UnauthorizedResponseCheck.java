package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;

@Rule(key = OAR035UnauthorizedResponseCheck.KEY)
public class OAR035UnauthorizedResponseCheck extends AbstractSecurityResponseCheck {

    public static final String KEY = "OAR035";
    private static final String MESSAGE = "OAR035.error";
    private static final String RESPONSE_CODES_STR = "401";

    public OAR035UnauthorizedResponseCheck() {
        super(KEY, MESSAGE, RESPONSE_CODES_STR);
    }
}