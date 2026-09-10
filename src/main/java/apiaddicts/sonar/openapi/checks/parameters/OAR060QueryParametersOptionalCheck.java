package apiaddicts.sonar.openapi.checks.parameters;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.sonar.sslr.api.AstNode;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import apiaddicts.sonar.openapi.utils.JsonNodeUtils;

@Rule(key = OAR060QueryParametersOptionalCheck.KEY)
public class OAR060QueryParametersOptionalCheck extends AbstractParameterCheck {

    public static final String KEY = "OAR060";
    private static final String MESSAGE = "OAR060.error";
    private static final String DEFAULT_EXCLUSION = "/status";
    private static final String PATHS = "paths";
    private static final String PARAMETERS = "parameters";
    private static final String REF = "$ref";
    private static final int MAX_REF_DEPTH = 10;

    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;

    private Set<String> exclusion = Collections.emptySet();

    private Map<String, Set<String>> refUsages = Collections.emptyMap();

    @Override
    protected void visitFile(JsonNode root) {
        exclusion = (exclusionStr == null || exclusionStr.trim().isEmpty())
                ? Collections.emptySet()
                : Arrays.stream(exclusionStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet());
        refUsages = collectRefUsages(root);
        super.visitFile(root);
    }

    @Override
    protected void visitParameterNode(JsonNode node) {

        JsonNode inNode = node.get("in");

        if (inNode != null && "query".equals(inNode.getTokenValue())) {

            if (isExcludedPath(node)) {
                return;
            }

            JsonNode requiredNode = node.get("required");

            if (requiredNode != null && "true".equals(requiredNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE), requiredNode);
            }
        }
    }

    private boolean isExcludedPath(JsonNode node) {
        AstNode pathNode = node.getFirstAncestor(
                OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
        if (pathNode != null) {
            return exclusion.contains(((JsonNode) pathNode).key().getTokenValue());
        }
        Set<String> usages = refUsages.get(node.getPointer());
        return usages != null && exclusion.containsAll(usages);
    }

    private Map<String, Set<String>> collectRefUsages(JsonNode root) {
        JsonNode paths = root.get(PATHS);
        if (paths.isMissing() || !paths.isObject()) {
            return Collections.emptyMap();
        }
        Map<String, Set<String>> usages = new HashMap<>();
        for (JsonNode pathNode : paths.properties()) {
            JsonNode pathKey = pathNode.key();
            if (pathKey.isMissing()) {
                continue;
            }
            String path = pathKey.getTokenValue();
            JsonNode pathItem = resolveLocalRef(pathNode);
            collectParameterRefUsages(pathItem.get(PARAMETERS), path, usages);
            for (JsonNode operationNode : pathItem.properties()) {
                if (JsonNodeUtils.isOperation(operationNode)) {
                    collectParameterRefUsages(operationNode.get(PARAMETERS), path, usages);
                }
            }
        }
        return usages;
    }

    private void collectParameterRefUsages(JsonNode parametersNode, String path, Map<String, Set<String>> usages) {
        if (parametersNode.isMissing() || !parametersNode.isArray()) {
            return;
        }
        for (JsonNode parameterNode : parametersNode.elements()) {
            JsonNode current = parameterNode;
            for (int depth = 0; depth < MAX_REF_DEPTH && current.isRef(); depth++) {
                JsonNode resolved = resolveLocalRef(current);
                if (resolved == current) {
                    break;
                }
                usages.computeIfAbsent(resolved.getPointer(), k -> new HashSet<>()).add(path);
                current = resolved;
            }
        }
    }

    private static JsonNode resolveLocalRef(JsonNode node) {
        if (!node.isRef()) {
            return node;
        }
        String ref = node.get(REF).getTokenValue();
        if (ref == null || !ref.startsWith("#")) {
            return node;
        }
        JsonNode resolved = node.resolve();
        return resolved.isMissing() ? node : resolved;
    }
}
