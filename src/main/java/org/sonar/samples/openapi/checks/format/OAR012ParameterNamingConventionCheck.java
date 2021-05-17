package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR012ParameterNamingConventionCheck.KEY)
public class OAR012ParameterNamingConventionCheck extends AbstractNamingConventionCheck {

	public static final String KEY = "OAR012";
	private static final String MESSAGE = "OAR012.error";

	private static Set<String> nameExceptions = new HashSet<>(Arrays.asList("$start", "$limit", "$total", "$expand", "$orderby", "$select", "$exclude"));

	private static final String DEFAULT_NAMING_CONVENTION = SNAKE_CASE;
	
	@RuleProperty(
			key = "default-naming-convention",
			description = "Default naming convention (snake_case, kebab-case, camelCase or UpperCamelCase).",
			defaultValue = DEFAULT_NAMING_CONVENTION)
	private static String defaultNamingConvention = DEFAULT_NAMING_CONVENTION;

	public OAR012ParameterNamingConventionCheck() {
		super(KEY, MESSAGE, nameExceptions);
		super.defaultNamingConvention = defaultNamingConvention;
	}

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

}