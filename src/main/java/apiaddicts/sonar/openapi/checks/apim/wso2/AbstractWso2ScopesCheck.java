package apiaddicts.sonar.openapi.checks.apim.wso2;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import apiaddicts.sonar.openapi.utils.JsonNodeUtils;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Set;

public abstract class AbstractWso2ScopesCheck extends BaseCheck {

	private JsonNode scopesKeyNode;

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi31Grammar.ROOT, OpenApi32Grammar.ROOT);
	}

	@Override
	public void visitNode(JsonNode node) {
		visitV2NV3Node(node);
	}

	protected JsonNode scopesKeyNode() {
		return scopesKeyNode;
	}

	private void visitV2NV3Node(JsonNode node) {
		JsonNode apimNode = JsonNodeUtils.getWso2ApimNode(node);
		JsonNode scopesNode = apimNode.get(JsonNodeUtils.WSO2_SCOPES);
		scopesKeyNode = JsonNodeUtils.propertyKey(apimNode, JsonNodeUtils.WSO2_SCOPES);
		visitScopesNode(scopesNode);
		if (scopesNode.isMissing() || scopesNode.isNull()) return;
		List<JsonNode> scopes = JsonNodeUtils.getWso2Scopes(scopesNode);
		visitScopes(scopes);
		scopes.forEach(this::visitScope);
	}

	protected void visitScopesNode(JsonNode scopesNode) {
		// Intentional blank
	}

	protected void visitScopes(List<JsonNode> scopes) {
		// Intentional blank
	}

	protected abstract void visitScope(JsonNode scope);
}
