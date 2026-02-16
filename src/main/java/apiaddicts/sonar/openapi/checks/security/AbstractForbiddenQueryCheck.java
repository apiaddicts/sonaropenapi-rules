package apiaddicts.sonar.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.RuleProperty;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractForbiddenQueryCheck extends BaseCheck {

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";

    @RuleProperty(
            key = "paths",
            description = "List of explicit paths to include/exclude from this rule separated by comma",
            defaultValue = "/examples"
    )
    protected String pathsStr;

    @RuleProperty(
            key = "pathValidationStrategy",
            description = "Path validation strategy (include/exclude)",
            defaultValue = "/include"
    )
    protected String pathCheckStrategy;

    protected Set<String> paths = new HashSet<>();
    protected String currentPath;

    @RuleProperty(
            key = "forbiddenItems",
            description = "List of forbidden query params or formats separated by comma",
            defaultValue = ""
    )
    protected String forbiddenItemsStr;

    protected Set<String> forbiddenItems = new HashSet<>();
    protected final String ruleKey;
    protected final String messageKey;

    protected AbstractForbiddenQueryCheck(String ruleKey, String messageKey) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
    }

    @Override
    protected void visitFile(JsonNode root) {
        // Inicializar paths
        if (pathsStr != null && !pathsStr.trim().isEmpty()) {
            paths = Stream.of(pathsStr.split(",")).map(String::trim).collect(Collectors.toSet());
        }

        // Inicializar forbiddenItems
        if (forbiddenItemsStr != null && !forbiddenItemsStr.trim().isEmpty()) {
            forbiddenItems = Stream.of(forbiddenItemsStr.split(",")).map(String::trim).collect(Collectors.toSet());
        }

        super.visitFile(root);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH,
                OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        AstNodeType type = node.getType();

        if (type == OpenApi2Grammar.PATH || type == OpenApi3Grammar.PATH || type == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
            return;
        }

        String operation = node.key().getTokenValue().toLowerCase();
        boolean isMethod = ImmutableSet.of("get", "post", "put", "patch", "delete").contains(operation);

        if (shouldExcludePath() || !isMethod) return;

        JsonNode parameters = node.get("parameters");
        if (parameters == null || parameters.isMissing() || parameters.isNull()) return;

        validateParameters(parameters, type == OpenApi2Grammar.OPERATION);
    }

    protected abstract void validateParameters(JsonNode parametersNode, boolean isV2);

    private boolean shouldExcludePath() {
        if (PATH_STRATEGY_EXCLUDE.equals(pathCheckStrategy)) {
            return paths.contains(currentPath);
        } else if (PATH_STRATEGY_INCLUDE.equals(pathCheckStrategy)) {
            return !paths.contains(currentPath);
        }
        return false;
    }
}