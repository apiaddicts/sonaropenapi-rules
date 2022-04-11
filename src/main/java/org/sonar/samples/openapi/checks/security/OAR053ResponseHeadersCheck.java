package org.sonar.samples.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR053ResponseHeadersCheck.KEY)
public class OAR053ResponseHeadersCheck extends BaseCheck {

    public static final String KEY = "OAR053";
    private static final String MANDATORY_HEADERS = "X-Trace-ID";
    private static final String ALLOWED_HEADERS = "idCorrelacion, X-CorrelacionId, X-Global-Trasaction-Id, x-power-by, X-Trace-ID";

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
    
    private Set<String> mandatoryHeaders = new HashSet<>();
    private Set<String> allowedHeaders = new HashSet<>();

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryHeadersStr.trim().isEmpty()) mandatoryHeaders.addAll(Stream.of(mandatoryHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        if (!allowedHeadersStr.trim().isEmpty()) allowedHeaders.addAll(Stream.of(allowedHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
    }

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.RESPONSES, OpenApi3Grammar.RESPONSES);
	}

	@Override
	public void visitNode(JsonNode node) {
        visitResponsesNode(node);
    }
    
    private void visitResponsesNode(JsonNode node) {
        List<JsonNode> allResponses = node.properties().stream().collect(Collectors.toList());
        for (JsonNode responseNode : allResponses) {
            responseNode = resolve(responseNode);
            visitResponseNode(responseNode);
        }
    }

	private void visitResponseNode(JsonNode node) {
        List<JsonNode> headerNameNodes = node.get("headers").propertyMap().values().stream().collect(Collectors.toList());
        List<String> headerNames = headerNameNodes.stream().map(headerNode -> headerNode.key().getTokenValue().toLowerCase().trim())
                .collect(Collectors.toList());
        if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && !headerNames.containsAll(mandatoryHeaders)) {
            addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), node.key());
        }

        for (JsonNode nodeName : headerNameNodes) {
            String headerName = nodeName.key().getTokenValue().toLowerCase().trim();
            if (allowedHeaders != null && !allowedHeaders.isEmpty() && !allowedHeaders.contains(headerName)) {
                addIssue(KEY, translate("generic.not-allowed-header"), nodeName.value());
            }
        }
	}
}
