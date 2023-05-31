package org.sonar.samples.openapi.checks.parameters;

import java.util.Set;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.samples.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Arrays;
import java.util.HashSet;

@Rule(key = OAR020ExpandParameterCheck.KEY)
public class OAR020ExpandParameterCheck extends BaseCheck {

    public static final String KEY = "OAR020";
    private static final String MESSAGE = "OAR020.error";
    private static final String DEFAULT_EXCLUDE_PATHS = "/status, /another";
    private static final String PARAM_NAME = "$expand";

    @RuleProperty(
        key = "excludePaths",
        description = "Comma-separated paths where the rule should be excluded",
        defaultValue = DEFAULT_EXCLUDE_PATHS)
    private String excludePaths = DEFAULT_EXCLUDE_PATHS;

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = "$expand"
    )
    private String parameterName = PARAM_NAME;

    private Set<String> exclusion;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER);
    }

    @Override
    protected void visitFile(JsonNode root) {
        exclusion = new HashSet<>(Arrays.asList(excludePaths.trim().split("\\s*,\\s*")));
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitParameterNode(node);
    }

    public void visitParameterNode(JsonNode node) {
        JsonNode inNode = node.get("in");
        JsonNode nameNode = node.get("name");

        if (inNode != null && nameNode != null) {
            String path = getPath(node);
            if (!isExcludedPath(path) && !parameterName.equals(nameNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE, parameterName), nameNode);
            }
        }
    }

    private String getPath(JsonNode node) {
        StringBuilder pathBuilder = new StringBuilder();
        AstNode pathNode = node.getFirstAncestor(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
        if (pathNode != null) {
            while (pathNode.getType() != OpenApi2Grammar.PATH && pathNode.getType() != OpenApi3Grammar.PATH) {
                pathNode = pathNode.getParent();
            }
            pathBuilder.append(((JsonNode) pathNode).key().getTokenValue());
        }
        return pathBuilder.toString();
    }

    private boolean isExcludedPath(String path) {
        return exclusion.contains(path);
    }
}