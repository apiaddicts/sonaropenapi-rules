package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR061GetMethodCheck;

public class OAR061GetMethodCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR061";
        check = new OAR061GetMethodCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2InsufficentResponseCodes() {
        verifyV2("insuficent-response-codes");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3InsufficentResponseCodes() {
        verifyV3("insuficent-response-codes");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR061 - GetMethod - Response codes in GET operations must be defined according to the standard", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("mandatory-response-codes", "200, 202, 206", RuleParamType.STRING);
        assertParameterProperties("paths", "/status, /another", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/exclude", RuleParamType.STRING);
    }
}