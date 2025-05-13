package apiaddicts.sonar.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR035UnauthorizedResponseCheck.KEY)
public class OAR035UnauthorizedResponseCheck extends BaseCheck {
    public static final String KEY = "OAR035";
    public static final String MESSAGE = "OAR035.error";

    private static final String RESPONSE_CODES_STR = "401";

    @RuleProperty(
        key = "expected-codes",
        description = "Expected response codes.",
        defaultValue = RESPONSE_CODES_STR
    )
    private String expectedCodesStr = RESPONSE_CODES_STR;
    private Set<String> expectedCodes;

    private boolean hasGlobalSecurity = false;

    @Override
    public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

	@Override
	protected void visitFile(JsonNode root) {
        expectedCodes = Arrays.stream(expectedCodesStr.split(",")).map(String::trim).collect(Collectors.toSet());
        hasGlobalSecurity = hasSecurity(root);
	}

    @Override
    public void visitNode(JsonNode node) {
        validateSecurityResponse(node);
    }

    private void validateSecurityResponse(JsonNode node) {
        JsonNode responsesNode = node.get("responses");
        Set<String> currentCodes = responsesNode.properties().stream().map(JsonNode::key).map(JsonNode::getTokenValue).collect(Collectors.toSet());
		Set<String> copyExpectedCodes = expectedCodes.stream().collect(Collectors.toSet());
        copyExpectedCodes.removeAll(currentCodes);
        if (hasSecurity(node)) {
            validateExpectedCodes(copyExpectedCodes, responsesNode);
		} else if (hasGlobalSecurity) {
            validateExpectedCodes(copyExpectedCodes, responsesNode);
		}
    }

    private void validateExpectedCodes(Set<String> copyExpectedCodes, JsonNode responsesNode) {
        for (String missingCode : copyExpectedCodes.stream().sorted().collect(Collectors.toList())) {
            addIssue(KEY, translate(MESSAGE, missingCode), responsesNode.key());
        }
    }

	private boolean hasSecurity(JsonNode node) {
		JsonNode security = node.get("security");
		if (security.isMissing() || security.isNull() || security.elements() == null || security.elements().isEmpty()) return false;
        return true;
	}
}
