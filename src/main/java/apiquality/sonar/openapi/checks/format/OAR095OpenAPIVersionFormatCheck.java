package apiquality.sonar.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiquality.sonar.openapi.checks.BaseCheck;

import java.util.Set;
import java.util.regex.Pattern;

@Rule(key = OAR095OpenAPIVersionFormatCheck.KEY)
public class OAR095OpenAPIVersionFormatCheck extends BaseCheck {

    public static final String KEY = "OAR095";
    private static final String MESSAGE = "OAR095.error";
    private static final Pattern VERSION_FORMAT = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.INFO, OpenApi3Grammar.INFO);
    }

    @Override
    public void visitNode(JsonNode node) {
        JsonNode versionNode = node.get("version");
        if (versionNode != null && !isCorrectVersionFormat(versionNode.getTokenValue())) {
            addIssue(KEY, translate(MESSAGE), versionNode.key());
        }
    }

    private boolean isCorrectVersionFormat(String version) {
        return VERSION_FORMAT.matcher(version).matches();
    }
}