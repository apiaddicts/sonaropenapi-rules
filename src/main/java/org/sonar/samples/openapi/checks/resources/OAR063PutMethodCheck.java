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

@Rule(key = OAR063PutMethodCheck.KEY)
public class OAR063PutMethodCheck extends BaseCheck {

    public static final String KEY = "OAR063";
    private static final String MESSAGE = "OAR063.error";
    private static final String MANDATORY_RESPONSE_CODES = "200 or 202 or 204 or 206";
    private static final String DEFAULT_EXCLUSION = "/status";

    @RuleProperty(
            key = "mandatory-response-codes",
            description = "List of allowed response codes. Or separated",
            defaultValue = MANDATORY_RESPONSE_CODES
    )
    private String mandatoryResponseCodesStr = MANDATORY_RESPONSE_CODES;

    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;

    private Set<String> mandatoryResponseCodes = new HashSet<>();
    private Set<String> exclusion;
    private String currentPath;

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryResponseCodesStr.trim().isEmpty()) {
            mandatoryResponseCodes.addAll(Stream.of(mandatoryResponseCodesStr.split(" or ")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        }
        if (!exclusionStr.trim().isEmpty()) {
            exclusion = Arrays.stream(exclusionStr.split(" or ")).map(String::trim).collect(Collectors.toSet());
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
            if (!"put".equalsIgnoreCase(operationType)) { 
                return;
            }

            JsonNode responsesNode = node.get("responses");
            if (responsesNode.isMissing() || responsesNode.isNull()) {
                addIssue(KEY, translate(MESSAGE, String.join(" or ", mandatoryResponseCodes)), responsesNode.key());
                return;
            }

            Set<String> responseCodes = responsesNode.propertyNames().stream().map(String::trim).collect(Collectors.toSet());

            boolean hasMandatoryCode = mandatoryResponseCodes.stream().anyMatch(responseCodes::contains);

            if (!hasMandatoryCode) {
                addIssue(KEY, translate(MESSAGE, String.join(" or ", mandatoryResponseCodes)), responsesNode.key());
            }
        }
    }
}