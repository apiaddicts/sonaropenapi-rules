package apiaddicts.sonar.openapi.checks.operations;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR017ResourcePathCheck.KEY)
public class OAR017ResourcePathCheck extends BaseCheck {

	public static final String KEY = "OAR017";
	private static final String MESSAGE = "OAR017.error";
	private static final String MESSAGE_PATTERN = "OAR017.error-patterns";
	public static final String EXCLUDE_PATTERNS = "get,me,search";

	@RuleProperty(
			key = "exclude_patterns",
			description = "List of exlude pattenrs separated by coma.",
			defaultValue = EXCLUDE_PATTERNS)
	public String patternsString = EXCLUDE_PATTERNS;

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		visitV2Node(node);
	}

	private void visitV2Node(JsonNode node) {
		String path = node.key().getTokenValue();
		if (!isCorrect(path,node)) addIssue(KEY, translate(MESSAGE), node.key());
	}

	private boolean isCorrect(String path, JsonNode node) {
		String[] parts = Stream.of(path.split("/")).filter(p -> !p.trim().isEmpty()).toArray(String[]::new);
		String[] patterns = Stream.of(patternsString.split(",")).toArray(String[]::new);
		if (parts.length == 0) return true;

		boolean previousWasVariable = false;
		boolean twoOrMoreVariablesInARow = false;

		for (int i = 0; i < parts.length; i++) {
		boolean currentIsVariable = isVariable(parts[i]);

			if(!currentIsVariable && Arrays.asList(patterns).contains(parts[i])){
				issuePatterns(parts[i],node);
			}

			if (previousWasVariable && currentIsVariable) {
				twoOrMoreVariablesInARow = true;
				break;
			}

			previousWasVariable = currentIsVariable;
		}

		return !twoOrMoreVariablesInARow;
	}

	private boolean isVariable(String part) {
		return '{' == part.charAt(0) && '}' == part.charAt(part.length() - 1);
	}

	private void issuePatterns(String pattern,JsonNode node){
		addIssue(KEY, translate(MESSAGE_PATTERN,pattern), node.key());
	}
}