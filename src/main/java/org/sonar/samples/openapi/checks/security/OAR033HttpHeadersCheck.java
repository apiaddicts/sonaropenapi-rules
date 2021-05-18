package org.sonar.samples.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR033HttpHeadersCheck.KEY)
public class OAR033HttpHeadersCheck extends BaseCheck {

    public static final String KEY = "OAR033";
    private static final String MANDATORY_HEADERS = "x-api-key";
    private static final String ALLOWED_HEADERS = "x-api-key, traceId, dateTime";
    private static final String FORBIDDEN_HEADERS = "Accept, Content-Type, Authorization";
    private static final String DEFAULT_EXCLUSION = "/status";

    @RuleProperty(
            key = "mandatory-headers",
            description = "List of mandatory headers. Comma separated",
            defaultValue = MANDATORY_HEADERS
    )
    private String mandatoryHeadersStr = MANDATORY_HEADERS;

    @RuleProperty(
            key = "allowed-headers",
            description = "List of allowed headers. Comma separated",
            defaultValue = ALLOWED_HEADERS
    )
    private String allowedHeadersStr = ALLOWED_HEADERS;

    @RuleProperty(
            key = "forbidden-headers",
            description = "List of forbidden headers. Comma separated",
            defaultValue = FORBIDDEN_HEADERS
    )
    private String forbiddenHeadersStr = FORBIDDEN_HEADERS;

    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;
    
    private Set<String> mandatoryHeaders = new HashSet<>();
    private Set<String> allowedHeaders = new HashSet<>();
    private Set<String> forbiddenHeaders = new HashSet<>();
    private Set<String> exclusion;

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryHeadersStr.isBlank()) mandatoryHeaders.addAll(Stream.of(mandatoryHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        allowedHeaders.addAll(Stream.of(allowedHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        forbiddenHeaders.addAll(Stream.of(forbiddenHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi2Grammar.PATH, OpenApi3Grammar.PARAMETER,
                OpenApi3Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi2Grammar.PATH) {
            visitPathV2Node(node);
        } else if (node.getType() == OpenApi3Grammar.PATH) {
            visitPathV3Node(node);
        } else {
            visitParameterNode(node);
        }
    }

    private void visitPathV2Node(JsonNode node) {
        String path = node.key().getTokenValue();
        if (exclusion.contains(path)) return;
        Collection<JsonNode> operationNodes = node.properties().stream().filter(this::isOperation).collect(Collectors.toList());
        for (JsonNode operationNode : operationNodes) {
            JsonNode parametersNode = operationNode.get("parameters");
            List<String> headerNames = listHeaderParameters(parametersNode);
            if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && !headerNames.containsAll(mandatoryHeaders)) {
                addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), operationNode.key());
            }
        }
    }

    private void visitPathV3Node(JsonNode node) {
        String path = node.key().getTokenValue();
        if (exclusion.contains(path)) return;
        JsonNode parametersInPathNode = node.get("parameters");
        List<String> headerNamesInPath = listHeaderParameters(parametersInPathNode);
        Collection<JsonNode> operationNodes = node.properties().stream().filter(this::isOperation).collect(Collectors.toList());
        for (JsonNode operationNode : operationNodes) {
            JsonNode parametersInOperationNode = operationNode.get("parameters");
            List<String> headerNamesInOperation = listHeaderParameters(parametersInOperationNode);
            headerNamesInOperation.addAll(headerNamesInPath);
            if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && !headerNamesInOperation.containsAll(mandatoryHeaders)) {
                addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), operationNode.key());
            }
        }
    }

    private List<String> listHeaderParameters(JsonNode parametersNode) {
        if (parametersNode.isMissing() || parametersNode.isNull()) return new ArrayList<String>();
        List<JsonNode> headerParametersNodes = parametersNode.elements().stream().filter(this::isHeaderParam).collect(Collectors.toList());
        if (headerParametersNodes.isEmpty()) return new ArrayList<String>();
        for (JsonNode headerParameterNode : headerParametersNodes) {
            JsonNode headerNameNode = headerParameterNode.resolve().get("name");
            String headerName = headerNameNode.getTokenValue().toLowerCase();
            JsonNode requiredProperty = headerParameterNode.resolve().get("required");
            if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && mandatoryHeaders.contains(headerName)
                && ( requiredProperty.isMissing() || requiredProperty.isNull() || !Boolean.parseBoolean(requiredProperty.getTokenValue()) )
            ) {
                addIssue(KEY, translate("OAR033.error-header-required", headerParameterNode.resolve().get("name").getTokenValue()), headerNameNode.value());
            }
        }
        return headerParametersNodes.stream().map(headerNode -> headerNode.resolve().get("name").getTokenValue().toLowerCase()).collect(Collectors.toList());
    }

    private void visitParameterNode(JsonNode node) {
        if (!"header".equals(node.get("in").getTokenValue())) return;
        JsonNode nodeName = node.get("name");
        String headerName = nodeName.getTokenValue().toLowerCase();
        if (!allowedHeaders.contains(headerName) || forbiddenHeaders.contains(headerName)) {
            addIssue(KEY, translate("OAR033.error-not-allowed-header"), nodeName.value());
        }
    }

    private boolean isOperation(JsonNode node) {
        AstNodeType type = node.getType();
        return type.equals(OpenApi2Grammar.OPERATION) || type.equals(OpenApi3Grammar.OPERATION);
    }

    private boolean isHeaderParam(JsonNode n) {
        return n.resolve().at("/in").getTokenValue().equals("header");
    }
}
