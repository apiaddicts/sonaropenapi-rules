package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import java.util.List;

@Rule(key = OAR097ShortBasePathCheck.KEY)
public class OAR097ShortBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR097";

    public OAR097ShortBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        List<String> parts = splitPath(path);
        if (parts.size() < 2) {
            addIssue(KEY, translate("OAR097.error-path-short"), node.value());
        }
    }
}