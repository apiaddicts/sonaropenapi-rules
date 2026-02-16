package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR066SnakeCaseNamingConventionCheck.KEY)
public class OAR066SnakeCaseNamingConventionCheck extends AbstractSchemaNamingConventionCheck {

    public static final String KEY = "OAR066";
    private static final String MESSAGE = "OAR066.error";

    public OAR066SnakeCaseNamingConventionCheck() {
        super(KEY, MESSAGE, SNAKE_CASE);
    }
}