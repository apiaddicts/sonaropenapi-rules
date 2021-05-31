package org.sonar.samples.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR053ResponseHeadersCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR053";
        check = new OAR053ResponseHeadersCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("mandatory-headers", "X-Trace-ID", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR053 - ResponseHeaders - Response headers required", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
}
