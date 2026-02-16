package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import java.util.List;

@Rule(key = OAR098LongBasePathCheck.KEY)
public class OAR098LongBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR098";

    private static final int DEFAULT_LENGTH = 2;

    @RuleProperty(
        key = "long-base-path",
        description = "Maximum allowed number of parts in the base path",
        defaultValue = "" + DEFAULT_LENGTH
    )
    private int maxParts = DEFAULT_LENGTH;

    public OAR098LongBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        List<String> parts = splitPath(path);
        if (parts.size() > maxParts) {
            addIssue(KEY, translate("OAR098.error-path-long"), node.value());
        }
    }
}