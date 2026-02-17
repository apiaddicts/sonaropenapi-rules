package apiaddicts.sonar.openapi.checks.owasp;

import apiaddicts.sonar.openapi.checks.security.AbstractPathAwareOperationCheck;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.RuleProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractPathResponseCheck extends AbstractPathAwareOperationCheck {

    protected final String ruleKey;
    protected final String messageKey;
    protected final String defaultPaths;
    protected final String defaultStrategy;

    protected static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    protected static final String PATH_STRATEGY_INCLUDE = "/include";

    @RuleProperty(
        key = "paths",
        description = "List of explicit paths to include/exclude from this rule separated by comma",
        defaultValue = ""
    )
    protected String pathsStr;

    @RuleProperty(
        key = "pathValidationStrategy",
        description = "Path validation strategy (include/exclude)",
        defaultValue = "/exclude"
    )
    protected String pathCheckStrategy;

    protected Set<String> paths;

    protected AbstractPathResponseCheck(
        String ruleKey,
        String messageKey,
        String defaultPaths,
        String defaultStrategy
    ) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.defaultPaths = defaultPaths;
        this.defaultStrategy = defaultStrategy;
        this.pathsStr = defaultPaths;
        this.pathCheckStrategy = defaultStrategy;
    }

    @Override
    protected void visitFile(JsonNode root) {
        paths = parsePaths(pathsStr);
        super.visitFile(root);
    }

    @Override
    protected void handleOperation(JsonNode node, AstNodeType type) {
        if (shouldSkipNode(currentPath)) return;
        validateOperation(node, currentPath);
    }

    protected boolean shouldSkipNode(String path) {
        if (PATH_STRATEGY_EXCLUDE.equals(pathCheckStrategy)) {
            return paths.contains(path);
        }
        if (PATH_STRATEGY_INCLUDE.equals(pathCheckStrategy)) {
            return !paths.contains(path);
        }
        return false;
    }

    protected Set<String> parsePaths(String pathsStr) {
        if (pathsStr == null || pathsStr.trim().isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(pathsStr.split(","))
            .map(String::trim)
            .collect(Collectors.toSet());
    }

    protected abstract void validateOperation(JsonNode node, String currentPath);
}