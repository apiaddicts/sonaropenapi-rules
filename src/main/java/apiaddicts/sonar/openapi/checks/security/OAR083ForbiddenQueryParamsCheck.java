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
        if (node.getType() == OpenApi2Grammar.PATH || node.getType() == OpenApi3Grammar.PATH || node.getType() == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
        } else if (node.getType() == OpenApi2Grammar.OPERATION || node.getType() == OpenApi3Grammar.OPERATION || node.getType() == OpenApi31Grammar.OPERATION) {
            if (shouldExcludePath()) {
                return;
            }

            String operationType = node.key().getTokenValue();
            if (!ImmutableSet.of("get", "post", "put", "patch", "delete").contains(operationType.toLowerCase())) {
                return;
            }

            JsonNode parametersNode = node.get("parameters");
            if (parametersNode == null || parametersNode.isNull()) {
                return;
            }
            Set<String> queryParams = new HashSet<>();
            parametersNode.elements().forEach(parameterNode -> {
                JsonNode inNode = parameterNode.get("in");
                if (inNode != null && "query".equals(inNode.getTokenValue())) {
                    JsonNode nameNode = parameterNode.get("name");
                    if (nameNode != null && !nameNode.isNull()) {
                        queryParams.add(nameNode.getTokenValue());
                    }
                }
            });
            String forbiddenParamsStr = forbiddenQueryParams.stream().filter(queryParams::contains).collect(Collectors.joining(", "));
            if (!forbiddenParamsStr.isEmpty()) {
                addIssue(KEY, translate(MESSAGE, forbiddenParamsStr), parametersNode.key());
            }
        }
    }

    private boolean shouldExcludePath() {
        if ("/exclude".equals(pathCheckStrategy)) {
            return paths.contains(currentPath);
        } else if ("/include".equals(pathCheckStrategy)) {
            return !paths.contains(currentPath);
        }
        return false;
    }
}