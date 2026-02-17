package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR040StandardWso2ScopesNameCheck.KEY)
public class OAR040StandardWso2ScopesNameCheck extends AbstractPatternWso2ScopesCheck {

    public static final String KEY = "OAR040";
    private static final String MESSAGE = "OAR040.error";
    private static final String DEFAULT_PATTERN_VALUE = "^[a-zA-Z]{4,}_(SC|sc)_[a-zA-Z0-9]{1,}$";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression used to check the 'name' field against.",
            defaultValue = DEFAULT_PATTERN_VALUE)
    private String patternStr = DEFAULT_PATTERN_VALUE;

    public OAR040StandardWso2ScopesNameCheck() {
        super(KEY, MESSAGE, "name", DEFAULT_PATTERN_VALUE);
    }
}