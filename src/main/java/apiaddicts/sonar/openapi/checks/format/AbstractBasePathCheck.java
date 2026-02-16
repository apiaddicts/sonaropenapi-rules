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

public abstract class AbstractBasePathCheck extends BaseCheck {

    protected final String ruleKey;

    protected AbstractBasePathCheck(String ruleKey) {
        this.ruleKey = ruleKey;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.SERVER, OpenApi31Grammar.SERVER);
    }

    @Override
    public void visitNode(JsonNode node) {
        String path;
        JsonNode sourceNode;

        if (node.getType() instanceof OpenApi2Grammar) {
            sourceNode = node.get("basePath");
            path = sourceNode.getTokenValue();
        } else {
            sourceNode = node.get("url");
            String url = sourceNode.getTokenValue();
            try {
                path = new URL(url).getPath();
            } catch (MalformedURLException e) {
                addIssue(ruleKey, translate("generic.malformed-url"), sourceNode.value());
                return;
            }
        }

        validateBasePath(path, sourceNode);
    }

    protected abstract void validateBasePath(String path, JsonNode node);

    protected List<String> splitPath(String path) {
        return Stream.of(path.split("/"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    protected boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }
}