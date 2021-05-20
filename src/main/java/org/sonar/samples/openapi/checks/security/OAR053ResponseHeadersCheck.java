package org.sonar.samples.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR053ResponseHeadersCheck.KEY)
public class OAR053ResponseHeadersCheck extends BaseCheck {

    public static final String KEY = "OAR053";
    private static final String MANDATORY_HEADERS = "x-trace-id";

    @RuleProperty(
            key = "mandatory-headers",
            description = "List of mandatory headers. Comma separated",
            defaultValue = MANDATORY_HEADERS
    )
    private String mandatoryHeadersStr = MANDATORY_HEADERS;
    
    private Set<String> mandatoryHeaders = new HashSet<>();

    @Override
    protected void visitFile(JsonNode root) {
        mandatoryHeaders.addAll(Stream.of(mandatoryHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
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
            String statusCode = responseNode.key().getTokenValue();
            responseNode = resolve(responseNode);
            visitResponseNode(responseNode);
        }
    }

	private void visitResponseNode(JsonNode node) {
        List<String> headerNames = node.get("headers").propertyMap().values()
                .stream().map(headerNode -> headerNode.key().getTokenValue())
                .collect(Collectors.toList());
        if (!headerNames.containsAll(mandatoryHeaders)) {
            addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), node.key());
        }
	}
}
