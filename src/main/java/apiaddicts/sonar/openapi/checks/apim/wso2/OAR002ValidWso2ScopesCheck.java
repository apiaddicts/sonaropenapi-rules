package apiaddicts.sonar.openapi.checks.apim.wso2;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.Token;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.utils.JsonNodeUtils;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Rule(key = OAR002ValidWso2ScopesCheck.KEY)
public class OAR002ValidWso2ScopesCheck extends AbstractWso2ScopesCheck {

	public static final String KEY = "OAR002";
	private static final String MESSAGE = "OAR002.error";
	private static final String MESSAGE_PROP = "OAR002.error-property";
	private static final Set<String> NULL_SPELLINGS = ImmutableSet.of("~", "Null", "NULL");

	private JsonNode scopesNode;

	@Override
	protected void visitScopesNode(JsonNode scopesNode) {
		if (scopesNode.isNull()) {
			addIssue(KEY, translate(MESSAGE), scopesNode.key());
		} else if (scopesNode.isMissing() && scopesKeyNode() != null) {
			addIssue(KEY, translate(MESSAGE), scopesKeyNode());
		}
		this.scopesNode = scopesNode;
	}

	@Override
	protected void visitScopes(List<JsonNode> scopes) {
		if (scopes.isEmpty()) addIssue(KEY, translate(MESSAGE), scopesNode.key());
	}

	@Override
	protected void visitScope(JsonNode scope) {
		Map<String, JsonNode> props = scope.propertyMap();
		validateProperty(props, "name", scope);
		validateProperty(props, "key", scope);
		validateProperty(props, "roles", scope);
	}

	private void validateProperty(Map<String, JsonNode> properties, String propertyName, JsonNode scope) {
		JsonNode property = properties.get(propertyName);
		if (property == null) {
			addIssue(KEY, translate(MESSAGE_PROP, propertyName), scopeLocation(scope));
			return;
		}
		if (property.isMissing()) {
			JsonNode key = JsonNodeUtils.propertyKey(scope, propertyName);
			addIssue(KEY, translate(MESSAGE_PROP, propertyName), key != null ? key : scopeLocation(scope));
		} else if (isEmpty(property)) {
			addIssue(KEY, translate(MESSAGE_PROP, propertyName), property.key());
		}
	}

	private JsonNode scopeLocation(JsonNode scope) {
		JsonNode key = scope.key();
		return key.isMissing() ? scope : key;
	}

	private boolean isEmpty(JsonNode property) {
		if (isNullScalar(property)) return true;
		if (property.isArray()) return property.elements().isEmpty();
		if (property.isObject()) return property.propertyMap().isEmpty();
		return property.getTokenValue().trim().equals("");
	}

	private boolean isNullScalar(JsonNode property) {
		if (property.isNull()) return true;
		Token token = property.getToken();
		return token != null && NULL_SPELLINGS.contains(token.getOriginalValue());
	}
}
