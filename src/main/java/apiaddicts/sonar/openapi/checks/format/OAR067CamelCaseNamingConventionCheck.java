package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR067CamelCaseNamingConventionCheck.KEY)
public class OAR067CamelCaseNamingConventionCheck extends AbstractSchemaNamingConventionCheck {

    public static final String KEY = "OAR067";
    private static final String MESSAGE = "OAR067.error";

    public OAR067CamelCaseNamingConventionCheck() {
        super(KEY, MESSAGE, CAMEL_CASE);
    }
}