package apiquality.sonar.openapi.checks.resources;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiquality.sonar.openapi.utils.VerbPathMatcher;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import static apiquality.sonar.openapi.utils.VerbPathMatcher.*;

@Rule(key = OAR018ResourcesByVerbCheck.KEY)
public class OAR018ResourcesByVerbCheck extends AbstractVerbPathCheck {

	public static final String KEY = "OAR018";
	private static final String MESSAGE = "OAR018.error";

	private static final String DEFAULT_RESOURCES_BY_VERB = 
		GET_ALL_1ST_LEVEL + GET_ALL_2ND_LEVEL + GET_ALL_3RD_LEVEL +
		GET_ONE_1ST_LEVEL + GET_ONE_2ND_LEVEL + GET_ONE_3RD_LEVEL +
		POST_1ST_LEVEL + POST_2ND_LEVEL + POST_3RD_LEVEL +
		POST_GET_1ST_LEVEL + POST_GET_2ND_LEVEL + POST_GET_3RD_LEVEL +
		POST_DELETE_1ST_LEVEL + POST_DELETE_2ND_LEVEL + POST_DELETE_3RD_LEVEL +
		PUT_1ST_LEVEL + PUT_2ND_LEVEL + PUT_3RD_LEVEL +
		PATCH_1ST_LEVEL + PATCH_2ND_LEVEL + PATCH_3RD_LEVEL +
		DELETE_1ST_LEVEL + DELETE_2ND_LEVEL + DELETE_3RD_LEVEL;

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
