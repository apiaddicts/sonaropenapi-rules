package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import java.util.List;

@Rule(key = OAR100LastPartBasePathCheck.KEY)
public class OAR100LastPartBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR100";

    public OAR100LastPartBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        List<String> parts = splitPath(path);
        if (parts.size() > 1
            && (!parts.get(parts.size() - 1).toLowerCase().startsWith("v") || !isInteger(parts.get(parts.size() - 1).substring(1)))) {
            addIssue(KEY, translate("OAR100.error-version"), node.value());
        }
    }
}