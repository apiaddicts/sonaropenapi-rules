package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR068PascalCaseNamingConventionCheck.KEY)
public class OAR068PascalCaseNamingConventionCheck extends AbstractSchemaNamingConventionCheck {

    public static final String KEY = "OAR068";
    private static final String MESSAGE = "OAR068.error";

    public OAR068PascalCaseNamingConventionCheck() {
        super(KEY, MESSAGE, PASCAL_CASE);
    }
}