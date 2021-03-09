package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.stream.Stream;

@Rule(key = OAR017ResourcePathCheck.KEY)
public class OAR017ResourcePathCheck extends BaseCheck {

	public static final String KEY = "OAR017";
	private static final String MESSAGE = "OAR017.error";

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		visitV2Node(node);
	}

	private void visitV2Node(JsonNode node) {
		String path = node.key().getTokenValue();
		if (!isCorrect(path)) addIssue(KEY, translate(MESSAGE), node.key());
	}

	private boolean isCorrect(String path) {
		String[] parts = Stream.of(path.split("/")).filter(p -> !p.trim().isEmpty()).toArray(String[]::new);
		if (parts.length == 0) return true;

		boolean previousIsVar = isVariable(parts[0]);
		if (previousIsVar) return false;

		for (int i = 1; i < parts.length; i++) {
			boolean currentIsVar = i < parts.length - 1 ? isVariable(parts[i]) : (isVariable(parts[i]) || parts[i].equals("get") || parts[i].equals("delete"));
			if (previousIsVar == currentIsVar) return false;
			previousIsVar = currentIsVar;
		}

		return true;
	}

	private boolean isVariable(String part) {
		return '{' == part.charAt(0) && '}' == part.charAt(part.length()-1);
	}
}
