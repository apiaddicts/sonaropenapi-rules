package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR066SnakeCaseNamingConventionCheck.KEY)
public class OAR066SnakeCaseNamingConventionCheck extends AbstractSchemaNamingConventionCheck {

    public static final String KEY = "OAR066";
    private static final String MESSAGE = "OAR066.error";

    public OAR066SnakeCaseNamingConventionCheck() {
        super(KEY, MESSAGE, SNAKE_CASE);
    }

    @Override
    protected void validateNamingConvention(String name, JsonNode nameNode) {
        if (name.startsWith("@") || name.startsWith("x-")) return;
        super.validateNamingConvention(name, nameNode);
    }
}