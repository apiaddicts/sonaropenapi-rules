package org.sonar.samples.openapi.checks.resources;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.samples.openapi.utils.VerbPathMatcher;
import org.sonar.sslr.yaml.grammar.JsonNode;

import static org.sonar.samples.openapi.utils.VerbPathMatcher.*;

@Rule(key = OAR018ResourcesByVerbCheck.KEY)
public class OAR018ResourcesByVerbCheck extends AbstractVerbPathCheck {

	public static final String KEY = "OAR018";
	private static final String MESSAGE = "OAR018.error";

	private static final String DEFAULT_RESOURCES_BY_VERB = GET_ALL + GET_ONE + GET_ALL_SUB_RESOURCES + POST + POST_GET + POST_DELETE + 
		POST_SUB_RESOURCE + POST_SUB_RESOURCE_GET + POST_SUB_RESOURCE_DELETE + PUT + PUT_SUB_RESOURCE + DELETE + DELETE_SUB_RESOURCE + PATCH + PATCH_SUB_RESOURCE;

	@RuleProperty(
			key = "allowed-resources-paths",
			description = "List of allowed resources path. Format: <V>,<V>:<RX>,<RX>;<V>,<V>:<RX>,<RX>",
			defaultValue = DEFAULT_RESOURCES_BY_VERB)
	private String pattern = DEFAULT_RESOURCES_BY_VERB;

	public OAR018ResourcesByVerbCheck() {
		super(KEY);
	}

	@Override
	protected void visitFile(JsonNode root) {
		matcher = new VerbPathMatcher(pattern);
	}

	@Override
	protected void mismatchV2(JsonNode node, JsonNode operationNode, String verb, String path) {
		addIssue(KEY, translate(MESSAGE, verb.toUpperCase(), path), operationNode.key());
	}
}
