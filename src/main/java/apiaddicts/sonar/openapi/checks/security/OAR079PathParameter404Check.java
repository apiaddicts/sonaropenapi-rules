package apiaddicts.sonar.openapi.checks.security;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR079PathParameter404Check.KEY)
public class OAR079PathParameter404Check extends BaseCheck {

    public static final String KEY = "OAR079";
    private static final String MESSAGE = "OAR079.error";
    private static final String DEFAULT_PATH = "/status";
    private static final String PATH_STRATEGY = "/exclude";

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";

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

    private Set<String> paths;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

    @Override
    protected void visitFile(JsonNode root) {
        paths = parsePaths(pathsStr);
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitOperationNode(node);
    }

    private void visitOperationNode(JsonNode node) {
        String currentPath = getCurrentPath(node);
        boolean isExcluded = PATH_STRATEGY_EXCLUDE.equals(pathCheckStrategy) && paths.contains(currentPath);
        boolean isNotIncluded = PATH_STRATEGY_INCLUDE.equals(pathCheckStrategy) && !paths.contains(currentPath);

        if (isExcluded || isNotIncluded) return;

        JsonNode parametersNode = node.get("parameters");
        if (parametersNode == null || !parametersNode.isArray()) return;

        for (JsonNode parameterNode : parametersNode.elements()) {
            if (isPathParameter(parameterNode)) {
                JsonNode responsesNode = node.get("responses");
                if (responsesNode.get("404").isMissing()) {
                    addIssue(KEY, translate(MESSAGE), responsesNode.key());
                    break;
                }
            }
        }
    }

    private boolean isPathParameter(JsonNode parameterNode) {
        JsonNode inNode = parameterNode.get("in");
        return inNode != null && "path".equals(inNode.getTokenValue());
    }

    private String getCurrentPath(JsonNode node) {
        JsonNode pathNode = (JsonNode) node.getFirstAncestor(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
        if (pathNode != null) {
            return pathNode.key().getTokenValue();
        }
        return "";
    }

    private Set<String> parsePaths(String pathsStr) {
        if (!pathsStr.trim().isEmpty()) {
            return Arrays.stream(pathsStr.split(",")).map(String::trim).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}