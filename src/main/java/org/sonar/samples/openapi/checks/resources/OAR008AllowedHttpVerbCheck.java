package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.isOperation;

@Rule(key = OAR008AllowedHttpVerbCheck.KEY)
public class OAR008AllowedHttpVerbCheck extends BaseCheck {

	public static final String KEY = "OAR008";
	private static final String MESSAGE = "OAR008.error";
	private static final String DEFAULT_ALLOWED_VERBS_VALUE = "get,post,put,delete,patch";

	@RuleProperty(
			key = "allowed-verbs",
			description = "List of allowed http verbs separated by coma.",
			defaultValue = DEFAULT_ALLOWED_VERBS_VALUE)
	public String allowedVerbsStr = DEFAULT_ALLOWED_VERBS_VALUE;

	private Set<String> allowedVerbs;

	@Override
	protected void visitFile(JsonNode root) {
		allowedVerbs = Stream.of(allowedVerbsStr.split(","))
				.map(String::trim)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());
	}

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		if (node.getType() == OpenApi2Grammar.OPERATION) {
			visitOperationNode(node);
		} else {
			visitV3PathNode(node);
		}
	}

	private void visitV3PathNode(JsonNode node) {
		node = node.resolve();
        Collection<JsonNode> operationNodes = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)).collect(Collectors.toList());
        for (JsonNode operationNode : operationNodes) {
			visitOperationNode(operationNode);
        }
	}

	private void visitOperationNode(JsonNode node) {
		String httpVerb = node.key().getTokenValue();
		if (!"$ref".equals(httpVerb) && !allowedVerbs.contains(httpVerb)) {
			addIssue(KEY, translate(MESSAGE, httpVerb), node.key());
		}
	}
}
