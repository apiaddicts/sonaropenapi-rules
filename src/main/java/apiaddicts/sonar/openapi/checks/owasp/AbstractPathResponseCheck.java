package apiaddicts.sonar.openapi.checks.owasp;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.RuleProperty;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractPathResponseCheck extends BaseCheck {

    protected final String ruleKey;
    protected final String messageKey;
    protected final String defaultPaths;
    protected final String defaultStrategy;

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
    protected String currentPath;

    protected static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    protected static final String PATH_STRATEGY_INCLUDE = "/include";

    protected AbstractPathResponseCheck(String ruleKey, String messageKey, String defaultPaths, String defaultStrategy) {
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

        if (type == OpenApi2Grammar.OPERATION || type == OpenApi3Grammar.OPERATION || type == OpenApi31Grammar.OPERATION) {
            if (shouldSkipNode(currentPath)) return;
            validateOperation(node, currentPath);
        }
    }

    protected boolean shouldSkipNode(String path) {
        if (PATH_STRATEGY_EXCLUDE.equals(pathCheckStrategy)) {
            return paths.contains(path);
        } else if (PATH_STRATEGY_INCLUDE.equals(pathCheckStrategy)) {
            return !paths.contains(path);
        }
        return false;
    }

    protected Set<String> parsePaths(String pathsStr) {
        if (!pathsStr.trim().isEmpty()) {
            return Arrays.stream(pathsStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    protected abstract void validateOperation(JsonNode node, String currentPath);
}