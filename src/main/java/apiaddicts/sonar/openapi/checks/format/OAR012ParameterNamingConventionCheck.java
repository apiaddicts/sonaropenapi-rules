package apiaddicts.sonar.openapi.checks.format;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.resolve;

@Rule(key = OAR012ParameterNamingConventionCheck.KEY)
public class OAR012ParameterNamingConventionCheck extends AbstractNamingConventionCheck {

	public static final String KEY = "OAR012";
	private static final String MESSAGE = "OAR012.error";

	private static Set<String> namExceptions = new HashSet<>(
			Arrays.asList("$init", "$start", "$limit", "$total", "$expand", "$orderby", "$select", "$exclude", "$filter"));

	private static final String NAMING_CONVENTION = SNAKE_CASE;

	@RuleProperty(key = "naming-convention", description = "Naming convention (snake_case, kebab-case, camelCase or UpperCamelCase).", defaultValue = NAMING_CONVENTION)
	private static String namConvention = NAMING_CONVENTION;

	public OAR012ParameterNamingConventionCheck() {
		super(KEY, MESSAGE, namConvention, namExceptions);
	}

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(
				OpenApi2Grammar.PARAMETER, OpenApi2Grammar.SCHEMA,
				OpenApi3Grammar.PARAMETER, OpenApi3Grammar.SCHEMA,
				OpenApi31Grammar.PARAMETER, OpenApi31Grammar.SCHEMA,
				OpenApi32Grammar.PARAMETER, OpenApi32Grammar.SCHEMA);
	}

	@Override
	public void visitNode(JsonNode node) {
		AstNodeType type = node.getType();
		if (OpenApi2Grammar.PARAMETER.equals(type) || OpenApi3Grammar.PARAMETER.equals(type)
				|| OpenApi31Grammar.PARAMETER.equals(type) || OpenApi32Grammar.PARAMETER.equals(type)) {
			visitParameterNode(node);
		} else {
			visitSchemaNode(node);
		}
	}

	private void visitSchemaNode(JsonNode schemaNode) {
		AstNodeType type = schemaNode.getType();
		if (OpenApi3Grammar.REF.equals(type) || OpenApi31Grammar.REF.equals(type) || OpenApi32Grammar.REF.equals(type))
			schemaNode = resolve(schemaNode);

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

	private void visitParameterNode(JsonNode node) {
		JsonNode inNode = node.get("in");
		String in = inNode.getTokenValue();
		if (!"path".equals(in) && !"query".equals(in))
			return;
		JsonNode nameNode = node.get("name");
		String name = nameNode.getTokenValue();
		validateNamingConvention(name, nameNode);
	}

}
