package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR012ParameterNamingConventionCheck.KEY)
public class OAR012ParameterNamingConventionCheck extends BaseCheck {
	
	private static final String CAMEL_REGEX = "[a-z]+([A-Z][a-z]+)*([A-Z])?";
	private static final String SNAKE_REGEX = "^[a-z0-9_$]*$";
	private static final String KEBAB_REGEX = "^[a-z0-9-]*$";

	private static final String SNAKE_CASE = "snake_case";
	private static final String KEBAB_CASE = "kebab-case";
	private static final String CAMEL_CASE = "camelCase";

	private static final String DEFAULT_NAMING_CONVENTION = SNAKE_CASE;

	public static final String KEY = "OAR012";
	private static final String MESSAGE = "OAR012.error";

	@RuleProperty(
			key = "default-naming-convention",
			description = "Default naming convention (snake_case, kebab-case or camelCase).",
			defaultValue = DEFAULT_NAMING_CONVENTION)
	public String defaultNamingConvention = DEFAULT_NAMING_CONVENTION;

	private Set<String> nameExceptions = new HashSet<>(Arrays.asList("$start", "$limit", "$total", "$expand", "$orderby", "$select", "$exclude"));

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER, OpenApi3Grammar.REQUEST_BODY,
				OpenApi3Grammar.RESPONSE);
	}

	@Override
	public void visitNode(JsonNode node) {
		if (OpenApi2Grammar.PARAMETER.equals(node.getType()) || OpenApi3Grammar.PARAMETER.equals(node.getType())) {
			visitParameterNode(node);
		} else {
			visitRequestBodyAndResponseNodeV3(node);
		}
	}

	private void visitRequestBodyAndResponseNodeV3(JsonNode node) {
		JsonNode contentNode = node.at("/content");
		if (contentNode.isMissing() || contentNode.isNull()) return;
		Map<String, JsonNode> mimeTypes = contentNode.propertyMap();
		for (JsonNode mimeType : mimeTypes.values()) {
			JsonNode schemaNode = mimeType.get("schema");
			if (schemaNode.isMissing()) return;
			if (schemaNode.getType().equals(OpenApi3Grammar.REF)) schemaNode = resolve(schemaNode);
		
			Map<String, JsonNode> properties = schemaNode.propertyMap();
			if (properties.containsKey("properties")) {
				Map<String, JsonNode> schemaProperties = schemaNode.get("properties").propertyMap();
				for (JsonNode property : schemaProperties.values()) {
					JsonNode nameNode = property.key();
					String name = nameNode.getTokenValue();
					validateNamingConvention(name, nameNode);
				}
			}

		}
	}

	private void visitParameterNode(JsonNode node) {
		JsonNode inNode = node.get("in");
		String in = inNode.getTokenValue();
		if (!"path".equals(in) && !"query".equals(in)) return;
		JsonNode nameNode = node.get("name");
		String name = nameNode.getTokenValue();
		validateNamingConvention(name, nameNode);
	}

	private void validateNamingConvention(String name, JsonNode nameNode) {
		if (nameExceptions.contains(name)) return;
		switch (defaultNamingConvention) {
			case CAMEL_CASE:
				if (!isCamelCase(name)) {
					addIssue(KEY, translate(MESSAGE, CAMEL_CASE), nameNode.key());
				}
				break;
			case KEBAB_CASE:
				if (!isKebabCase(name)) {
					addIssue(KEY, translate(MESSAGE, KEBAB_CASE), nameNode.key());
				}
				break;
			case SNAKE_CASE:
			default:
				if (!isSnakeCase(name)) {
					addIssue(KEY, translate(MESSAGE, SNAKE_CASE), nameNode.key());
				}
				break;
		}
	}

	private boolean isCamelCase(String name) {
		return name.split("_").length == 1 && name.split("-").length == 1 && name.matches(CAMEL_REGEX);
	}

	private boolean isSnakeCase(String name) {
		return name.matches(SNAKE_REGEX);
	}

	private boolean isKebabCase(String name) {
		return name.matches(KEBAB_REGEX);
	}
}