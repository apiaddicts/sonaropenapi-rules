package org.sonar.samples.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Set;

public abstract class AbstractWso2ScopesCheck extends BaseCheck {

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.ROOT);
	}

	@Override
	public void visitNode(JsonNode node) {
		visitV2Node(node);
	}

	private void visitV2Node(JsonNode node) {
		JsonNode scopesNode = node.get("x-wso2-security").get("apim").get("x-wso2-scopes");
		visitScopesNode(scopesNode);
		if (scopesNode.isMissing() || scopesNode.isNull()) return;
		List<JsonNode> scopes = scopesNode.elements();
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
