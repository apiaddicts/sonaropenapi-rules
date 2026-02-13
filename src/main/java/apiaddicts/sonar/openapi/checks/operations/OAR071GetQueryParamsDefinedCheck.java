package apiaddicts.sonar.openapi.checks.operations;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR071GetQueryParamsDefinedCheck.KEY)
public class OAR071GetQueryParamsDefinedCheck extends BaseCheck {

    public static final String KEY = "OAR071";
    private static final String MESSAGE = "OAR071.error";
    private static final String QUERY_PARAMS = "param1, param2, param3";
    private static final String DEFAULT_PATH = "/examples";
    private static final String PATH_STRATEGY = "/include";

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";

    @RuleProperty(
            key = "mandatory-query-params",
            description = "List of allowed query params separated by comma",
            defaultValue = QUERY_PARAMS
    )
    private String mandatoryQueryParamsStr = QUERY_PARAMS;

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

    private Set<String> mandatoryQueryParams = new HashSet<>();
    private Set<String> exclusion;
    private String currentPath;

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryQueryParamsStr.trim().isEmpty()) {
            mandatoryQueryParams.addAll(Stream.of(mandatoryQueryParamsStr.split(",")).map(String::trim).collect(Collectors.toSet()));
        }
        if (!pathsStr.trim().isEmpty()) {
            exclusion = Arrays.stream(pathsStr.split(",")).map(String::trim).collect(Collectors.toSet());
        } else {
            exclusion = new HashSet<>();
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

        if (isType(type, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH)) {
            currentPath = node.key().getTokenValue();
            return;
        }

        if (isType(type, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION)) {
            if (shouldExcludePath() || !"get".equalsIgnoreCase(node.key().getTokenValue())) {
                return;
            }

            JsonNode paramsNode = node.get("parameters");
            if (paramsNode == null || paramsNode.isNull() || paramsNode.isMissing()) {
                addIssue(KEY, translate(MESSAGE), node.key());
                return;
            }
            Set<String> queryParams = new HashSet<>();
            paramsNode.elements().forEach(p -> {
                JsonNode in = p.get("in");
                JsonNode name = p.get("name");
                if (in != null && "query".equals(in.getTokenValue()) && name != null && !name.isNull()) {
                    queryParams.add(name.getTokenValue());
                }
            });

            String missing = mandatoryQueryParams.stream()
                    .filter(p -> !queryParams.contains(p))
                    .collect(Collectors.joining(", "));

            if (!missing.isEmpty()) {
                addIssue(KEY, translate(MESSAGE, missing), paramsNode.key());
            }
        }
    }

    private boolean isType(AstNodeType nodeType, AstNodeType... types) {
        return Arrays.asList(types).contains(nodeType);
    }

    private boolean shouldExcludePath() {
        if (pathCheckStrategy.equals(PATH_STRATEGY_EXCLUDE)) {
            return exclusion.contains(currentPath);
        } else if (pathCheckStrategy.equals(PATH_STRATEGY_INCLUDE)) {
            return !exclusion.contains(currentPath);
        }
        return false;
    }
}