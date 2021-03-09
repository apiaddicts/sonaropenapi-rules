package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.regex.Matcher;

import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class AbstractResourceLevelCheck extends BaseCheck {

	private static final String MESSAGE = "generic.path-level";

	private String key;

	public AbstractResourceLevelCheck(String key) {
		this.key = key;
	}

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
		if (matchLevel(path)) addIssue(key, translate(MESSAGE), node.key());
	}

	private boolean matchLevel(String path) {
		String levelRegex = "\\/[^/{}]*\\/\\{[^/{}]*\\}";
		Pattern levelPattern = Pattern.compile(levelRegex);
		Matcher levelMatcher = levelPattern.matcher(path);
		Integer levels = 0;
		while ( levelMatcher.find() ) {
			levels++;
		}

		long pathParts = Stream.of(path.split("/")).filter(p -> !p.trim().isEmpty()).count();
		return matchLevel(pathParts - levels);

	}

	abstract boolean matchLevel(long level);
}
