package apiquality.sonar.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;

@Rule(key = OAR011UrlNamingConventionCheck.KEY)
public class OAR011UrlNamingConventionCheck extends AbstractNamingConventionCheck {

	public static final String KEY = "OAR011";
	private static final String MESSAGE = "OAR011.error";

	private static final String NAMING_CONVENTION = KEBAB_CASE;
	private static final Pattern KEBAB_SEGMENT = Pattern.compile("^[a-z0-9]+([-.][a-z0-9]+)*$");

	@RuleProperty(
			key = "naming-convention",
			description = "Naming convention (snake_case, kebab-case, camelCase or UpperCamelCase).",
			defaultValue = NAMING_CONVENTION)
	private static String namingConvention = NAMING_CONVENTION;

	public OAR011UrlNamingConventionCheck() {
		super(KEY, MESSAGE, namingConvention);
	}

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi2Grammar.PATH, OpenApi3Grammar.SERVER, OpenApi3Grammar.PATH, OpenApi31Grammar.SERVER, OpenApi31Grammar.PATH);
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

	@Override
	protected void validateNamingConvention(String name, JsonNode nameNode) {
		if (nameExceptions.contains(name)) return;
		if (KEBAB_CASE.equalsIgnoreCase(namingConvention)) {
			if (!isKebabCaseWithDots(name)) {
				addIssue(KEY, translate(MESSAGE, KEBAB_CASE), nameNode.key());
			}
			return;
		}
		super.validateNamingConvention(name, nameNode);
	}

	private boolean isKebabCaseWithDots(String path) {
		String[] segments = path.replaceAll("^/+", "").split("/");
		for (String segment : segments) {
			if (segment.isEmpty()) return false;
			if (segment.startsWith(".") || segment.endsWith(".")) return false;
			if (!KEBAB_SEGMENT.matcher(segment).matches()) return false;
		}
		return true;
	}
}