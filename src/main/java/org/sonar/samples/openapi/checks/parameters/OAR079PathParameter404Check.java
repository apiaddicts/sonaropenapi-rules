package org.sonar.samples.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.samples.openapi.checks.BaseCheck;

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
    private static final String DEFAULT_EXCLUSION = "/status, /health-check";
    private static final String DEFAULT_INCLUSION = "/example1, /example2";
    private static final String PATH_STRATEGY = "/exclude";

    @RuleProperty(
            key = "excludePaths",
            description = "List of explicit paths to exclude from this rule separated by comma",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;

    @RuleProperty(
            key = "includePaths",
            description = "List of explicit paths to include in this rule separated by comma",
            defaultValue = DEFAULT_INCLUSION
    )
    private String inclusionStr = DEFAULT_INCLUSION;

    @RuleProperty(
            key = "pathCheckStrategy",
            description = "Path check strategy (include/exclude)",
            defaultValue = PATH_STRATEGY
    )
    private String pathCheckStrategy = PATH_STRATEGY;

    private Set<String> exclusion;
    private Set<String> inclusion;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    protected void visitFile(JsonNode root) {
        if (pathCheckStrategy.equals("/exclude")) {
            exclusion = parsePaths(exclusionStr);
        } else if (pathCheckStrategy.equals("/include")) {
            inclusion = parsePaths(inclusionStr);
        }
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitOperationNode(node);
    }

    private void visitOperationNode(JsonNode node) {
        String currentPath = getCurrentPath(node);
        if (pathCheckStrategy.equals("/exclude")) {
            if (exclusion.contains(currentPath)) {
                return;
            }
        } else if (pathCheckStrategy.equals("/include")) {
            if (!inclusion.contains(currentPath)) {
                return;
            }
        }

        JsonNode responsesNode = node.get("responses");
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode != null && parametersNode.isArray()) {
            for (JsonNode parameterNode : parametersNode.elements()) {
                if (isPathParameter(parameterNode)) {
                    JsonNode responsesNode404 = responsesNode.get("404");
                    if (responsesNode404.isMissing()) {
                        addIssue(KEY, translate(MESSAGE), responsesNode.key());
                        break;
                    }
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