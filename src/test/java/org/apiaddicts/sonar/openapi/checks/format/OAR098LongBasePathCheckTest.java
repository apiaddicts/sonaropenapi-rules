package org.apiaddicts.sonar.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.format.OAR098LongBasePathCheck;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR098LongBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR098";
        check = new OAR098LongBasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2TooLong() {
        verifyV2("too-long");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3TooLong() {
        verifyV3("too-long");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR098 - LongBasePath - Base path must not have more than two parts", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("long-base-path", "2", RuleParamType.INTEGER);
    }
}