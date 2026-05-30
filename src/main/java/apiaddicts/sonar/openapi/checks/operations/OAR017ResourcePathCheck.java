package apiaddicts.sonar.openapi.checks.operations;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR017ResourcePathCheck.KEY)
public class OAR017ResourcePathCheck extends BaseCheck {

	public static final String KEY = "OAR017";
	private static final String MESSAGE = "OAR017.error";
	public static final String EXCLUDE_PATTERNS = "get,me,search";

	@RuleProperty(
			key = "exclude_patterns",
			description = "List of exlude pattenrs separated by coma.",
			defaultValue = EXCLUDE_PATTERNS)
	public String patternsString = EXCLUDE_PATTERNS;

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		String path = node.key().getTokenValue();
		if (!isCorrect(path)) addIssue(KEY, translate(MESSAGE), node.key());
	}

	private boolean isCorrect(String path) {
		String[] parts = Stream.of(path.split("/")).filter(p -> !p.trim().isEmpty()).toArray(String[]::new);
		List<String> except = Arrays.asList(patternsString.split(","));

		if (parts.length == 0) return true;

		boolean previousIsVar;
		String firstPart = parts[0];

		if (except.contains(firstPart.trim())) {
			previousIsVar = true;
		} else if (isVariable(firstPart)) {
			return false;
		} else {
			previousIsVar = false;
		}

		for (int i = 1; i < parts.length; i++) {
			String part = parts[i].trim();

			if (except.contains(part)) {
				previousIsVar = true;
				continue;
			}

			boolean currentIsVariable = isVariable(part);
			if (currentIsVariable == previousIsVar) {
				return false;
			}
			previousIsVar = currentIsVariable;
		}

		return true;
	}

	private boolean isVariable(String part) {
		return part.length() >= 2 && part.charAt(0) == '{' && part.charAt(part.length() - 1) == '}';
	}
}