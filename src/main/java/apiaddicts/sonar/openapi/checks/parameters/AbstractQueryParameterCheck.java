package apiaddicts.sonar.openapi.checks.parameters;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

public abstract class AbstractQueryParameterCheck extends BaseCheck {

    protected static final String DEFAULT_PATH = "/examples";
    protected static final String PATH_STRATEGY = "/include";

    protected static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    protected static final String PATH_STRATEGY_INCLUDE = "/include";

    protected static final String PAGINATED_RESPONSE_CODE = "206";

    protected final String ruleKey;
    protected final String messageKey;
    protected final String defaultParameterName;
    protected final boolean applyToParameterizedPaths;

    protected List<Pattern> paths;
    protected JsonNode rootNode;

    protected AbstractQueryParameterCheck(
        String ruleKey,
        String messageKey,
        String defaultParameterName,
        boolean applyToParameterizedPaths
    ) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.defaultParameterName = defaultParameterName;
        this.applyToParameterizedPaths = applyToParameterizedPaths;
    }

    protected abstract String getPathsStr();

    protected abstract String getPathCheckStrategy();

    protected String getParameterName() {
        return defaultParameterName;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION, OpenApi32Grammar.OPERATION);
    }

    @Override
    protected void visitFile(JsonNode root) {
        this.rootNode = root;
        paths = parsePaths(getPathsStr());
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

        if (requiresPaginatedResponse() && !hasPaginatedResponse(node)) {
            return;
        }

        if (!shouldIncludePath(path)) {
            return;
        }

        JsonNode parameterNode = findMatchingParameter(node);

        if (parameterNode == null) {
            addIssue(
                ruleKey,
                translate(messageKey, getParameterName()),
                node.key()
            );
        } else if (getExpectedType() != null && !hasExpectedType(parameterNode)) {
            addIssue(
                ruleKey,
                translate(getTypeMessageKey(), getParameterName()),
                node.key()
            );
        }
    }

    protected boolean hasParameterInNode(JsonNode node) {
        return findMatchingParameter(node) != null;
    }

    protected JsonNode findMatchingParameter(JsonNode node) {
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode != null) {
            for (JsonNode parameterNode : parametersNode.elements()) {
                if (isRefParameter(parameterNode)) {
                    JsonNode refParameterNode = resolveReference(parameterNode.get("$ref").getTokenValue(), rootNode);
                    if (refParameterNode != null && matchesNameAndIn(refParameterNode)) {
                        return refParameterNode;
                    }
                } else if (matchesNameAndIn(parameterNode)) {
                    return parameterNode;
                }
            }
        }
        return null;
    }

    protected boolean isRefParameter(JsonNode parameterNode) {
        JsonNode refNode = parameterNode.get("$ref");
        return refNode != null && !refNode.isMissing();
    }

    protected boolean hasNamedRefParameter(JsonNode parameterNode) {
        JsonNode refParameterNode = resolveReference(parameterNode.get("$ref").getTokenValue(), rootNode);
        return refParameterNode != null && matchesNameAndIn(refParameterNode);
    }

    protected boolean hasDirectParameter(JsonNode parameterNode) {
        return matchesNameAndIn(parameterNode);
    }

    protected boolean matchesNameAndIn(JsonNode parameterNode) {
        JsonNode nameNode = parameterNode.get("name");
        JsonNode inNode = parameterNode.get("in");
        return inNode != null && "query".equals(inNode.getTokenValue())
            && nameNode != null && getParameterName().equals(nameNode.getTokenValue());
    }

    protected String getExpectedType() {
        return null;
    }

    protected String getTypeMessageKey() {
        return null;
    }

    protected boolean hasExpectedType(JsonNode parameterNode) {
        String expected = getExpectedType();
        if (expected == null) {
            return true;
        }
        JsonNode schemaNode = parameterNode.get("schema");
        JsonNode typeNode;
        if (schemaNode != null && !schemaNode.isMissing()) {
            JsonNode schemaRef = schemaNode.get("$ref");
            if (schemaRef != null && !schemaRef.isMissing()) {
                schemaNode = resolveReference(schemaRef.getTokenValue(), rootNode);
            }
            typeNode = schemaNode != null ? schemaNode.get("type") : null;
        } else {
            typeNode = parameterNode.get("type"); // OpenAPI 2.0
        }
        return typeNode != null && !typeNode.isMissing() && expected.equals(typeNode.getTokenValue());
    }

    protected String getPath(JsonNode node) {
        StringBuilder pathBuilder = new StringBuilder();
        AstNode pathNode = node.getFirstAncestor(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
        if (pathNode != null) {
            while (pathNode.getType() != OpenApi2Grammar.PATH && pathNode.getType() != OpenApi3Grammar.PATH && pathNode.getType() != OpenApi31Grammar.PATH && pathNode.getType() != OpenApi32Grammar.PATH) {
                pathNode = pathNode.getParent();
            }
            pathBuilder.append(((JsonNode) pathNode).key().getTokenValue());
        }
        return pathBuilder.toString();
    }

    protected boolean requiresPaginatedResponse() {
        return false;
    }

    protected boolean hasPaginatedResponse(JsonNode node) {
        JsonNode responses = node.get("responses");
        if (responses == null || responses.isMissing()) {
            return false;
        }
        for (JsonNode responseNode : responses.propertyMap().values()) {
            if (PAGINATED_RESPONSE_CODE.equals(responseNode.key().getTokenValue())) {
                return true;
            }
        }
        return false;
    }

    protected boolean shouldIncludePath(String path) {
        if (paths.isEmpty()) {
            return getPathCheckStrategy().equals(PATH_STRATEGY_EXCLUDE);
        }
        boolean matchesList = paths.stream().anyMatch(p -> p.matcher(path).find());
        if (getPathCheckStrategy().equals(PATH_STRATEGY_EXCLUDE)) {
            return !matchesList;
        } else if (getPathCheckStrategy().equals(PATH_STRATEGY_INCLUDE)) {
            return matchesList;
        }
        return false;
    }

    protected boolean endsWithPathParam(String path) {
        String[] segments = path.split("/");
        if (segments.length == 0) return false;

        String last = segments[segments.length - 1].trim();
        return last.matches("^\\{[^}]+\\}$");
    }

    protected List<Pattern> parsePaths(String pathsStr) {
        if (pathsStr != null && !pathsStr.trim().isEmpty()) {
            return Arrays.stream(pathsStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(segment -> Pattern.compile(Pattern.quote(segment) + "(/|$)"))
                .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
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