package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import java.util.List;

@Rule(key = OAR042BasePathCheck.KEY)
public class OAR042BasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR042";

    public OAR042BasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        List<String> parts = splitPath(path);

        if (parts.size() < 2) {
            addIssue(KEY, translate("OAR042.error-path-short"), node.value());
        } else if (parts.size() > 2) {
            addIssue(KEY, translate("OAR042.error-path-long"), node.value());
        }

        if (!parts.isEmpty() && (!parts.get(0).startsWith("api-") || parts.get(0).length() == 4)) {
            addIssue(KEY, translate("OAR042.error-prefix"), node.value());
        }

        if (parts.size() > 1
            && (!parts.get(1).toLowerCase().startsWith("v") || !isInteger(parts.get(1).substring(1)))) {
            addIssue(KEY, translate("OAR042.error-version"), node.value());
        }
    }
}