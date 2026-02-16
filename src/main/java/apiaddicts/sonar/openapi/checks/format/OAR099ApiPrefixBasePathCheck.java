package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import java.util.List;

@Rule(key = OAR099ApiPrefixBasePathCheck.KEY)
public class OAR099ApiPrefixBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR099";

    public OAR099ApiPrefixBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        List<String> parts = splitPath(path);
        if (!parts.isEmpty() && (!parts.get(0).startsWith("api-") || parts.get(0).length() == 4)) {
            addIssue(KEY, translate("OAR099.error-prefix"), node.value());
        }
    }
}