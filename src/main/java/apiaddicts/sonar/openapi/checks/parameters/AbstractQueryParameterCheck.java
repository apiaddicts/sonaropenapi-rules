package apiaddicts.sonar.openapi.checks.parameters;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.RuleProperty;

public abstract class AbstractQueryParameterCheck extends BaseCheck {

    protected static final String DEFAULT_PATH = "/examples";
    protected static final String PATH_STRATEGY = "/include";

    protected final String ruleKey;
    protected final String messageKey;
    protected final String parameterName;
    protected final boolean applyToParameterizedPaths;

    protected Set<String> paths;
    protected JsonNode rootNode;

    @RuleProperty(
        key = "paths",
        description = "List of explicit paths to include/exclude from this rule separated by comma",
        defaultValue = DEFAULT_PATH
    )
    protected String pathsStr = DEFAULT_PATH;

    @RuleProperty(
        key = "pathValidationStrategy",
        description = "Path validation strategy (include/exclude)",
        defaultValue = PATH_STRATEGY
    )
    protected String pathCheckStrategy = PATH_STRATEGY;

    protected AbstractQueryParameterCheck(
        String ruleKey,
        String messageKey,
        String parameterName,
        boolean applyToParameterizedPaths
    ) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.parameterName = parameterName;
        this.applyToParameterizedPaths = applyToParameterizedPaths;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

    @Override
    protected void visitFile(JsonNode root) {
        this.rootNode = root;
        paths = parsePaths(pathsStr);
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (!"get".equals(node.key().getTokenValue())) {
            return;
        }

        String path = getPath(node);

        if (!applyToParameterizedPaths && endsWithPathParam(path)) {
            return;
        }

        boolean hasParameter = hasParameterInNode(node);

        if (shouldIncludePath(path) && !hasParameter) {
            addIssue(
                ruleKey,
                translate(messageKey, parameterName),
                node.key()
            );
        }
    }

    protected boolean hasParameterInNode(JsonNode node) {
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode != null) {

            for (JsonNode parameterNode : parametersNode.elements()) {
              if (isRefParameter(parameterNode) && hasNamedRefParameter(parameterNode)) {
                  return true;
              }
              if (hasDirectParameter(parameterNode)) {
                  return true;
              }
            }
        }
        return false;
    }

    protected boolean isRefParameter(JsonNode parameterNode) {
        return parameterNode.get("$ref") != null;
    }

    protected boolean hasNamedRefParameter(JsonNode parameterNode) {
        String refValue = parameterNode.get("$ref").getTokenValue();
        JsonNode refParameterNode = resolveReference(refValue, rootNode);  
        if (refParameterNode != null) {
            JsonNode nameNode = refParameterNode.get("name");
            JsonNode inNode = refParameterNode.get("in"); 
            return inNode != null && "query".equals(inNode.getTokenValue()) && nameNode != null && parameterName.equals(nameNode.getTokenValue());
        }
        return false;
    }

    protected boolean hasDirectParameter(JsonNode parameterNode) {
        JsonNode nameNode = parameterNode.get("name");
        JsonNode inNode = parameterNode.get("in");
        return inNode != null && "query".equals(inNode.getTokenValue()) && nameNode != null && parameterName.equals(nameNode.getTokenValue());
    }

    protected String getPath(JsonNode node) {
        StringBuilder pathBuilder = new StringBuilder();
        AstNode pathNode = node.getFirstAncestor(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
        if (pathNode != null) {
            while (pathNode.getType() != OpenApi2Grammar.PATH && pathNode.getType() != OpenApi3Grammar.PATH && pathNode.getType() != OpenApi31Grammar.PATH) {
                pathNode = pathNode.getParent();
            }
            pathBuilder.append(((JsonNode) pathNode).key().getTokenValue());
        }
        return pathBuilder.toString();
    }

    protected boolean shouldIncludePath(String path) {
        if (pathCheckStrategy.equals("/exclude")) {
            return !paths.contains(path);
        } else if (pathCheckStrategy.equals(PATH_STRATEGY)) {
            return paths.contains(path);
        }
        return false;
    }

    protected boolean endsWithPathParam(String path) {
        String[] segments = path.split("/");
        if (segments.length == 0) return false;

        String last = segments[segments.length - 1].trim();
        return last.matches("^\\{[^}]+\\}$");
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

    protected JsonNode resolveReference(String refValue, JsonNode root) {
        if (refValue == null || !refValue.startsWith("#/")) { 
            return null; 
        }

        String pathToReference = refValue.substring(2);
        String[] pathParts = pathToReference.split("/");

        JsonNode currentNode = root;
        for (String part : pathParts) {
            if (currentNode == null) {
                return null; 
            }
            currentNode = currentNode.get(part);
        }

        return currentNode;
    }
}