package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR071GetQueryParamsDefinedCheck.KEY)
public class OAR071GetQueryParamsDefinedCheck extends BaseCheck {

    public static final String KEY = "OAR071";
    private static final String MESSAGE = "OAR071.error";
    private static final String MANDATORY_QUERY_PARAMS = "param1, param2, param3";
    private static final String DEFAULT_EXCLUSION = "/status, /another";

    @RuleProperty(
            key = "mandatory-query-params",
            description = "List of allowed query params separated by comma",
            defaultValue = MANDATORY_QUERY_PARAMS
    )
    private String mandatoryQueryParamsStr = MANDATORY_QUERY_PARAMS;

    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule separated by comma",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;

    private Set<String> mandatoryQueryParams = new HashSet<>();
    private Set<String> exclusion;
    private String currentPath;

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryQueryParamsStr.trim().isEmpty()) {
            mandatoryQueryParams.addAll(Stream.of(mandatoryQueryParamsStr.split(",")).map(String::trim).collect(Collectors.toSet()));
        }
        if (!exclusionStr.trim().isEmpty()) {
            exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());
        } else {
            exclusion = new HashSet<>();
        }
        super.visitFile(root);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi2Grammar.PATH || node.getType() == OpenApi3Grammar.PATH) {
            currentPath = node.key().getTokenValue();
        } else if (node.getType() == OpenApi2Grammar.OPERATION || node.getType() == OpenApi3Grammar.OPERATION) {
            if (exclusion.contains(currentPath)) {
                return;
            }

            String operationType = node.key().getTokenValue();
            if (!"get".equalsIgnoreCase(operationType)) {
                return;
            }

            JsonNode parametersNode = node.get("parameters");
            Set<String> queryParams = new HashSet<>();
            parametersNode.elements().forEach(parameterNode -> {
                JsonNode inNode = parameterNode.get("in");
                if (inNode != null && "query".equals(inNode.getTokenValue())) {
                    JsonNode nameNode = parameterNode.get("name");
                    if (nameNode != null && !nameNode.isNull()) {
                        queryParams.add(nameNode.getTokenValue());
                    }
                }
            });
            boolean allMandatoryParamsDefined = mandatoryQueryParams.stream().allMatch(queryParams::contains);
            String missingParamsStr = mandatoryQueryParams.stream().filter(p -> !queryParams.contains(p)).collect(Collectors.joining(", "));
            if (!allMandatoryParamsDefined) {
                addIssue(KEY, translate(MESSAGE, missingParamsStr), parametersNode.key());
            }
        }
    }
}