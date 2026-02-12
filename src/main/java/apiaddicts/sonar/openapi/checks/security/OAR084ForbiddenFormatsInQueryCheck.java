package apiaddicts.sonar.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR084ForbiddenFormatsInQueryCheck.KEY)
public class OAR084ForbiddenFormatsInQueryCheck extends BaseCheck {

    public static final String KEY = "OAR084";
    private static final String MESSAGE = "OAR084.error";
    private static final String FORBIDDEN_QUERY_FORMATS = "password";
    private static final String DEFAULT_PATH = "/examples";
    private static final String PATH_STRATEGY = "/include";

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";

    @RuleProperty(
            key = "forbidden-query-formats",
            description = "List of forbidden query params separated by comma",
            defaultValue = FORBIDDEN_QUERY_FORMATS
    )
    private String forbiddenQueryFormatsStr = FORBIDDEN_QUERY_FORMATS;

    @RuleProperty(
            key = "paths",
            description = "List of explicit paths to include/exclude from this rule separated by comma",
            defaultValue = DEFAULT_PATH
    )
    private String pathsStr = DEFAULT_PATH;

    @RuleProperty(
            key = "pathValidationStrategy",
            description = "Path validation strategy (include/exclude)",
            defaultValue = PATH_STRATEGY
    )
    private String pathCheckStrategy = PATH_STRATEGY;

    private Set<String> forbiddenQueryFormats = new HashSet<>();
    private Set<String> paths;
    private String currentPath;

    @Override
    protected void visitFile(JsonNode root) {
        if (!forbiddenQueryFormatsStr.trim().isEmpty()) {
            forbiddenQueryFormats.addAll(Stream.of(forbiddenQueryFormatsStr.split(",")).map(String::trim).collect(Collectors.toSet()));
        }
        if (!pathsStr.trim().isEmpty()) {
            paths = Stream.of(pathsStr.split(",")).map(String::trim).collect(Collectors.toSet());
        } else {
            paths = new HashSet<>();
        }
        super.visitFile(root);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        AstNodeType type = node.getType();

        if (type == OpenApi2Grammar.PATH || type == OpenApi3Grammar.PATH || type == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
            return;
        }

        String op = node.key().getTokenValue().toLowerCase();
        boolean isMethod = ImmutableSet.of("get", "post", "put", "patch", "delete").contains(op);

        if (shouldExcludePath() || !isMethod) return;

        JsonNode params = node.get("parameters");
        if (params == null || params.isMissing() || params.isNull()) return;

        validateParameters(params, type == OpenApi2Grammar.OPERATION);
    }

    private void validateParameters(JsonNode params, boolean isV2) {
        List<JsonNode> violations = new ArrayList<>();

        params.elements().forEach(param -> {
            JsonNode in = param.get("in");
            if (in == null || !"query".equals(in.getTokenValue())) return;

            JsonNode formatNode = isV2 ? param.get("format") : param.get("schema").get("format");

            if (formatNode != null && !formatNode.isMissing() && !formatNode.isNull() 
                && forbiddenQueryFormats.contains(formatNode.getTokenValue())) {
                violations.add(formatNode);
            }
        });

        if (!violations.isEmpty()) {
            String forbiddenStr = violations.stream()
                    .map(JsonNode::getTokenValue)
                    .distinct()
                    .collect(Collectors.joining(", "));

            violations.forEach(node -> addIssue(KEY, translate(MESSAGE, forbiddenStr), node));
        }
    }

    private boolean shouldExcludePath() {
        if (PATH_STRATEGY_EXCLUDE.equals(pathCheckStrategy)) {
            return paths.contains(currentPath);
        } else if (PATH_STRATEGY_INCLUDE.equals(pathCheckStrategy)) {
            return !paths.contains(currentPath);
        }
        return false;
    }
}