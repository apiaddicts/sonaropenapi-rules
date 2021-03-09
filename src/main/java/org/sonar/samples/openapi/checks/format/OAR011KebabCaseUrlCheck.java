package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Rule(key = OAR011KebabCaseUrlCheck.KEY)
public class OAR011KebabCaseUrlCheck extends BaseCheck {

	private static final String PARAM_REGEX = "\\{[^}{]*}";
	private static final String KEBAB_REGEX = "^[a-z0-9-]*$";

	public static final String KEY = "OAR011";
	private static final String MESSAGE = "OAR011.error";

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi2Grammar.PATH, OpenApi3Grammar.SERVER, OpenApi3Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		if (node.getType() instanceof OpenApi2Grammar) {
			visitV2Node(node);
		} else {
			visitV3Node(node);
		}
	}

	private void visitV2Node(JsonNode node) {
		if (node.is(OpenApi2Grammar.ROOT)) {
			visitV2RootNode(node);
		} else {
			visitPathNode(node);
		}
	}

	private void visitV3Node(JsonNode node) {
		if (node.is(OpenApi3Grammar.SERVER)) {
			visitV3ServerNode(node);
		} else {
			visitPathNode(node);
		}
	}

	private void visitV2RootNode(JsonNode node) {
		JsonNode basePathNode = node.get("basePath");
		if (basePathNode.isMissing()) return;
		String path = basePathNode.getTokenValue();
		if (!isKebab(path)) addIssue(KEY, translate(MESSAGE), basePathNode.key());
	}

	private void visitV3ServerNode(JsonNode node) {
		JsonNode urlNode = node.get("url");
        String server = urlNode.getTokenValue();
        String path;
		try {
			path = new URL(server).getPath();
			if (!isKebab(path)) addIssue(KEY, translate(MESSAGE), urlNode.key());
		} catch (MalformedURLException e) {
			return;
		}
	}

	private void visitPathNode(JsonNode node) {
		String path = node.key().getTokenValue();
		if (!isKebab(path)) addIssue(KEY, translate(MESSAGE), node.key());
	}

	private boolean isKebab(String path) {
		String pathWithoutParams = path.replaceAll(PARAM_REGEX, "");
		String pathStr = pathWithoutParams.replace('/', '-');
		return pathStr.matches(KEBAB_REGEX);
	}
}