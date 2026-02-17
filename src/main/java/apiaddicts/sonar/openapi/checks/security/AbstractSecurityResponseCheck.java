package apiaddicts.sonar.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.RuleProperty;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSecurityResponseCheck extends BaseCheck {

    protected final String ruleKey;
    protected final String messageKey;
    protected final String defaultResponseCodes;

    @RuleProperty(
        key = "expected-codes",
        description = "Expected response codes.",
        defaultValue = ""
    )
    protected String expectedCodesStr;

    protected Set<String> expectedCodes;
    protected boolean hasGlobalSecurity = false;

    protected AbstractSecurityResponseCheck(String ruleKey, String messageKey, String defaultResponseCodes) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.defaultResponseCodes = defaultResponseCodes;
        this.expectedCodesStr = defaultResponseCodes;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

    @Override
    protected void visitFile(JsonNode root) {
        expectedCodes = Arrays.stream(expectedCodesStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        hasGlobalSecurity = hasSecurity(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        validateSecurityResponse(node);
    }

    private void validateSecurityResponse(JsonNode node) {
        JsonNode responsesNode = node.get("responses");
        Set<String> currentCodes = responsesNode.properties().stream()
                .map(JsonNode::key)
                .map(JsonNode::getTokenValue)
                .collect(Collectors.toSet());

        Set<String> missingCodes = expectedCodes.stream()
                .filter(code -> !currentCodes.contains(code))
                .collect(Collectors.toSet());

        if (hasSecurity(node) || hasGlobalSecurity) {
            validateExpectedCodes(missingCodes, responsesNode);
        }
    }

    private void validateExpectedCodes(Set<String> missingCodes, JsonNode responsesNode) {
        missingCodes.stream()
                .sorted()
                .forEach(code -> addIssue(ruleKey, translate(messageKey, code), responsesNode.key()));
    }

    protected boolean hasSecurity(JsonNode node) {
        JsonNode security = node.get("security");
        return !security.isMissing() && !security.isNull() && !security.elements().isEmpty();
    }
}