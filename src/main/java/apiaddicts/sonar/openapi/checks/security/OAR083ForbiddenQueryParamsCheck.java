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

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR083ForbiddenQueryParamsCheck.KEY)
public class OAR083ForbiddenQueryParamsCheck extends BaseCheck {

    public static final String KEY = "OAR083";
    private static final String MESSAGE = "OAR083.error";
    private static final String FORBIDDEN_QUERY_PARAMS = "email, password";
    private static final String DEFAULT_PATH = "/examples";
    private static final String PATH_STRATEGY = "/include";

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";

    @RuleProperty(
            key = "forbidden-query-params",
            description = "List of forbidden query params separated by comma",
            defaultValue = FORBIDDEN_QUERY_PARAMS
    )
    private String forbiddenQueryParamsStr = FORBIDDEN_QUERY_PARAMS;

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

    private Set<String> forbiddenQueryParams = new HashSet<>();
    private Set<String> paths;
    private String currentPath;

    @Override
    protected void visitFile(JsonNode root) {
        if (!forbiddenQueryParamsStr.trim().isEmpty()) {
            forbiddenQueryParams.addAll(Stream.of(forbiddenQueryParamsStr.split(",")).map(String::trim).collect(Collectors.toSet()));
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
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION , OpenApi31Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        AstNodeType type = node.getType();

        if (type == OpenApi2Grammar.PATH || type == OpenApi3Grammar.PATH || type == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
            return;
        }

        String operationType = node.key().getTokenValue().toLowerCase();
        boolean isMethod = ImmutableSet.of("get", "post", "put", "patch", "delete").contains(operationType);

        if (shouldExcludePath() || !isMethod) return;

        JsonNode params = node.get("parameters");
        if (params == null || params.isMissing() || params.isNull()) return;

        validateParameters(params);
    }

    private void validateParameters(JsonNode parametersNode) {
        Set<String> queryParams = new HashSet<>();
        parametersNode.elements().forEach(parameterNode -> {
            JsonNode in = parameterNode.get("in");
            JsonNode name = parameterNode.get("name");
            if (in != null && "query".equals(in.getTokenValue()) && name != null && !name.isNull()) {
                queryParams.add(name.getTokenValue());
            }
        });

        String forbidden = forbiddenQueryParams.stream()
                .filter(queryParams::contains)
                .collect(Collectors.joining(", "));

        if (!forbidden.isEmpty()) {
            addIssue(KEY, translate(MESSAGE, forbidden), parametersNode.key());
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