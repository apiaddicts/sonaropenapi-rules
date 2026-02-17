package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR004ValidWso2ScopesRolesCheck.KEY)
public class OAR004ValidWso2ScopesRolesCheck extends AbstractPatternWso2ScopesCheck {

    public static final String KEY = "OAR004";
    private static final String MESSAGE = "OAR004.error";
    private static final String DEFAULT_PATTERN_VALUE = "^[a-zA-Z0-9_\\-., ]+$";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression used to check the 'roles' field against.",
            defaultValue = DEFAULT_PATTERN_VALUE)
    private String patternStr = DEFAULT_PATTERN_VALUE;

    public OAR004ValidWso2ScopesRolesCheck() {
        super(KEY, MESSAGE, "roles", DEFAULT_PATTERN_VALUE);
    }
}