package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR065DeleteMethodCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR065";
        check = new OAR065DeleteMethodCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
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
        assertRuleProperties("OAR065 - DeleteMethod - Response codes in DELETE operations must be defined according to the standard", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("mandatory-response-codes", "200, 202, 204", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status, /another", RuleParamType.STRING);
    }
}