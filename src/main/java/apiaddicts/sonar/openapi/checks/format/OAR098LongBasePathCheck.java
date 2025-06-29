package apiaddicts.sonar.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR098LongBasePathCheck.KEY)
public class OAR098LongBasePathCheck extends BaseCheck {

    public static final String KEY = "OAR098";

    private static final int LONG_BASE_PATH = 2;

    @RuleProperty(key = "long-base-path", description = "long of the path in the basepath keywork value", defaultValue = "" + LONG_BASE_PATH )
	private static int basePahtLength = LONG_BASE_PATH;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.SERVER, OpenApi31Grammar.SERVER);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() instanceof OpenApi2Grammar) {
            visitV2Node(node);
        } else {
            visitV3ServerNode(node);
        }
    }

    private void visitV2Node(JsonNode node) {
        JsonNode basePathNode = node.get("basePath");
        String path = basePathNode.getTokenValue();
        validatePath(path, basePathNode);
    }

    private void visitV3ServerNode(JsonNode node) {
        JsonNode urlNode = node.get("url");
        String server = urlNode.getTokenValue();
        try {
            String path = new URL(server).getPath();
            validatePath(path, urlNode);
        } catch (MalformedURLException e) {
            addIssue(KEY, translate("generic.malformed-url"), urlNode.value());
        }
    }

    private void validatePath(String path, JsonNode node) {
        List<String> pathParts = Stream.of(path.split("/")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        if (pathParts.size() > basePahtLength) {
            addIssue(KEY, translate("OAR098.error-path-long"), node.value());  
        }
    }
}