package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Set;

@Rule(key = OAR035AuthorizationResponsesCheck.KEY)
public class OAR035AuthorizationResponsesCheck extends BaseCheck {
    public static final String KEY = "OAR035";

    private boolean hasGlobalSecurity = false;

    @Override
    public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

	@Override
	protected void visitFile(JsonNode root) {
		hasGlobalSecurity = hasSecurity(root);
	}

    @Override
    public void visitNode(JsonNode node) {
        validateSecurityResponse(node);
    }

    private void validateSecurityResponse(JsonNode node) {
        JsonNode responses = node.get("responses");

		if (hasSecurity(node)) {
            if (responses.get("401").isMissing() || responses.get("403").isMissing() || responses.get("429").isMissing()) {
                addIssue(KEY, translate("OAR035.error-security"), responses.key());
            }
		} else if (hasGlobalSecurity) {
            if (responses.get("401").isMissing() || responses.get("403").isMissing() || responses.get("429").isMissing()) {
                addIssue(KEY, translate("OAR035.error-security"), responses.key());
            }
		}
    }

	private boolean hasSecurity(JsonNode node) {
		JsonNode security = node.get("security");
		if (security.isMissing() || security.isNull() || security.elements() == null || security.elements().isEmpty()) return false;
        return true;
	}
}
