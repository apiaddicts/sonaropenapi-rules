package apiaddicts.sonar.openapi.checks.parameters;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR060QueryParametersOptionalCheck.KEY)
public class OAR060QueryParametersOptionalCheck extends AbstractParameterCheck {

    public static final String KEY = "OAR060";
    private static final String MESSAGE = "OAR060.error";
    private static final String DEFAULT_EXCLUSION = "/status";

    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;

    private Set<String> exclusion = Collections.emptySet();

    @Override
    protected void visitFile(JsonNode root) {
        exclusion = (exclusionStr == null || exclusionStr.trim().isEmpty())
                ? Collections.emptySet()
                : Arrays.stream(exclusionStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
        super.visitFile(root);
    }

    @Override
    protected void visitParameterNode(JsonNode node) {

        JsonNode inNode = node.get("in");

        if (inNode != null && "query".equals(inNode.getTokenValue())) {

            if (isExcludedPath(node)) {
                return;
            }

            JsonNode requiredNode = node.get("required");

            if (requiredNode != null && "true".equals(requiredNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE), requiredNode);
            }
        }
    }

    private boolean isExcludedPath(JsonNode node) {
        AstNode pathNode = node.getFirstAncestor(
                OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
        if (pathNode == null) {
            return false;
        }
        String path = ((JsonNode) pathNode).key().getTokenValue();
        return exclusion.contains(path);
    }
}
