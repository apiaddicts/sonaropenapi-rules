package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Rule(key = OAR011UrlNamingConventionCheck.KEY)
public class OAR011UrlNamingConventionCheck extends AbstractNamingConventionCheck {

	public static final String KEY = "OAR011";
	private static final String MESSAGE = "OAR011.error";

	private static final String DEFAULT_NAMING_CONVENTION = KEBAB_CASE;
	
	@RuleProperty(
			key = "default-naming-convention",
			description = "Default naming convention (snake_case, kebab-case, camelCase or UpperCamelCase).",
			defaultValue = DEFAULT_NAMING_CONVENTION)
	private static String defaultNamingConvention = DEFAULT_NAMING_CONVENTION;

	public OAR011UrlNamingConventionCheck() {
		super(KEY, MESSAGE);
		super.defaultNamingConvention = defaultNamingConvention;
	}

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
		validateNamingConvention(path, basePathNode.key());
	}

	private void visitV3ServerNode(JsonNode node) {
		JsonNode urlNode = node.get("url");
        String server = urlNode.getTokenValue();
        String path;
		try {
			path = new URL(server).getPath();
			validateNamingConvention(path, urlNode.key());
		} catch (MalformedURLException e) {
			return;
		}
	}

	private void visitPathNode(JsonNode node) {
		String path = node.key().getTokenValue();
		validateNamingConvention(path, node.key());
	}
}