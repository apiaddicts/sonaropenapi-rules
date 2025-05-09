package apiquality.sonar.openapi.checks.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import apiquality.sonar.openapi.checks.BaseCheck;
import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR053ResponseHeadersCheck.KEY)
public class OAR053ResponseHeadersCheck extends BaseCheck {

    public static final String KEY = "OAR053";
    private static final String MANDATORY_HEADERS = "X-Trace-ID";
    private static final String ALLOWED_HEADERS = "idCorrelacion, X-CorrelacionId, X-Global-Trasaction-Id, x-power-by, X-Trace-ID";
    private static final String INCLUDED_RESPONSE_CODES = "*";
    private static final String EXCLUDED_RESPONSE_CODES = "204";
    private static final String DEFAULT_EXCLUSION = "/status";

    @RuleProperty(key = "mandatory-headers", description = "List of mandatory headers. Comma separated", defaultValue = MANDATORY_HEADERS)
    private String mandatoryHeadersStr = MANDATORY_HEADERS;

    @RuleProperty(key = "allowed-headers", description = "List of allowed headers. Comma separated", defaultValue = ALLOWED_HEADERS)
    private String allowedHeadersStr = ALLOWED_HEADERS;

    @RuleProperty(key = "included-response-codes", description = "List of allowed response codes. Comma separated", defaultValue = INCLUDED_RESPONSE_CODES)
    private String includedResponseCodesStr = INCLUDED_RESPONSE_CODES;

    @RuleProperty(key = "excluded-response-codes", description = "List of excluded response codes. Comma separated", defaultValue = EXCLUDED_RESPONSE_CODES)
    private String excludedResponseCodesStr = EXCLUDED_RESPONSE_CODES;

    @RuleProperty(key = "path-exclusions", description = "List of explicit paths to exclude from this rule.", defaultValue = DEFAULT_EXCLUSION)
    private String exclusionStr = DEFAULT_EXCLUSION;

    private Set<String> mandatoryHeaders = new HashSet<>();
    private Set<String> allowedHeaders = new HashSet<>();
    private Set<String> includedResponseCodes = new HashSet<>();
    private Set<String> excludedResponseCodes = new HashSet<>();
    private Set<String> exclusion;

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryHeadersStr.trim().isEmpty())
            mandatoryHeaders.addAll(Stream.of(mandatoryHeadersStr.split(",")).map(header -> header.toLowerCase().trim())
                    .collect(Collectors.toSet()));
        if (!allowedHeadersStr.trim().isEmpty())
            allowedHeaders.addAll(Stream.of(allowedHeadersStr.split(",")).map(header -> header.toLowerCase().trim())
                    .collect(Collectors.toSet()));
        if (!includedResponseCodesStr.trim().isEmpty())
            includedResponseCodes.addAll(Stream.of(includedResponseCodesStr.split(","))
                    .map(code -> code.toLowerCase().trim()).collect(Collectors.toSet()));
        if (!excludedResponseCodesStr.trim().isEmpty())
            excludedResponseCodes.addAll(Stream.of(excludedResponseCodesStr.split(","))
                    .map(code -> code.toLowerCase().trim()).collect(Collectors.toSet()));
        if (!exclusionStr.trim().isEmpty())
            exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitPathNode(node);
    }

    private void visitPathNode(JsonNode node) {
        String path = node.key().getTokenValue();
        if (exclusion.contains(path))
            return;
        Collection<JsonNode> operationNodes = node.properties().stream()
                .filter(propertyNode -> isOperation(propertyNode)).collect(Collectors.toList());
        for (JsonNode operationNode : operationNodes) {
            visitResponsesNode(operationNode.at("/responses"));
        }
    }

    private void visitResponsesNode(JsonNode node) {
        List<JsonNode> allResponses = node.properties().stream().collect(Collectors.toList());
        for (JsonNode responseNode : allResponses) {
            String statusCode = responseNode.key().getTokenValue();

            if (excludedResponseCodes.contains(statusCode))
                continue;

            if (includedResponseCodes.contains(statusCode) || includedResponseCodes.contains("*")) {
                responseNode = resolve(responseNode);
                visitResponseNode(responseNode);
            }
        }
    }

    private void visitResponseNode(JsonNode node) {
        List<JsonNode> headerNameNodes = node.get("headers").propertyMap().values().stream()
                .collect(Collectors.toList());
        List<String> headerNames = headerNameNodes.stream()
                .map(headerNode -> headerNode.key().getTokenValue().toLowerCase().trim())
                .collect(Collectors.toList());
        if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && !headerNames.containsAll(mandatoryHeaders)) {
            addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), node.key());
        }

        for (JsonNode nodeName : headerNameNodes) {
            String headerName = nodeName.key().getTokenValue().toLowerCase().trim();
            if (allowedHeaders != null && !allowedHeaders.isEmpty() && !allowedHeaders.contains(headerName)) {
                addIssue(KEY, translate("generic.not-allowed-header"), nodeName.key());
            }
        }
    }
}
