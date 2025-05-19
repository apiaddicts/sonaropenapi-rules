package apiaddicts.sonar.openapi.checks.parameters;

import java.util.Set;
import java.util.stream.Collectors;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Arrays;
import java.util.HashSet;

@Rule(key = OAR025LimitParameterCheck.KEY)
public class OAR025LimitParameterCheck extends BaseCheck {

    public static final String KEY = "OAR025";
    private static final String MESSAGE = "OAR025.error";
    private static final String DEFAULT_PATH = "/examples";
    private static final String PATH_STRATEGY = "/include";
    private static final String PARAM_NAME = "$limit";

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

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = PARAM_NAME
    )
    private String parameterName = PARAM_NAME;

    private Set<String> paths;
    private JsonNode rootNode;  

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
        if ("get".equals(node.key().getTokenValue())) {

            String path = getPath(node);

            boolean hasParameter = hasParameterInNode(node);

            if (shouldIncludePath(path) && !hasParameter) {
                addIssue(KEY, translate(MESSAGE, PARAM_NAME), node.key());
            }
        }
    }

    private boolean hasParameterInNode(JsonNode node) {
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode != null) {

            for (JsonNode parameterNode : parametersNode.elements()) {
                if (isRefParameter(parameterNode) && hasNamedRefParameter(parameterNode)) {
                    return true;
                } else if (hasDirectParameter(parameterNode)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRefParameter(JsonNode parameterNode) {
        JsonNode refNode = parameterNode.get("$ref");
        if (refNode != null) {
            return true;
        }
        return false;
    }

    private boolean hasNamedRefParameter(JsonNode parameterNode) {
        String refValue = parameterNode.get("$ref").getTokenValue();
        JsonNode refParameterNode = resolveReference(refValue, rootNode);  
        if (refParameterNode != null) {
            JsonNode nameNode = refParameterNode.get("name");
            JsonNode inNode = refParameterNode.get("in"); 
            return inNode != null && "query".equals(inNode.getTokenValue()) && nameNode != null && parameterName.equals(nameNode.getTokenValue());
        }
        return false;
    }

    private boolean hasDirectParameter(JsonNode parameterNode) {
        JsonNode nameNode = parameterNode.get("name");
        JsonNode inNode = parameterNode.get("in");
        return inNode != null && "query".equals(inNode.getTokenValue()) && nameNode != null && parameterName.equals(nameNode.getTokenValue());
    }

    private String getPath(JsonNode node) {
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

    private boolean shouldIncludePath(String path) {
        if (pathCheckStrategy.equals("/exclude")) {
            return !paths.contains(path);
        } else if (pathCheckStrategy.equals("/include")) {
            return paths.contains(path);
        }
        return false;
    }

      private Set<String> parsePaths(String pathsStr) {
        if (!pathsStr.trim().isEmpty()) {
            return Arrays.stream(pathsStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    private JsonNode resolveReference(String refValue, JsonNode root) {
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
    
        if (currentNode == null) {
        } else {
        }
    
        return currentNode;
    }
}