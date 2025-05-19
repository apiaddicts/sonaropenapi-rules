package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.format.OAR088RefParamCheck;

public class OAR088RefParamCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR088";
        check = new OAR088RefParamCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("invalid-ref");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-ref");
    }

    @Test
    public void verifyInV3() {
        verifyV3("invalid-ref");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR088 - RefParam - The $ref of a parameter must end with a corresponding suffix", RuleType.BUG, Severity.MINOR, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("default-suffix", "Param", RuleParamType.STRING);
    }
}