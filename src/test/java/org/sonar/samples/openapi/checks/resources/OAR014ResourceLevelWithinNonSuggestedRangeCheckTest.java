package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR014ResourceLevelWithinNonSuggestedRangeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR014";
        check = new OAR014ResourceLevelWithinNonSuggestedRangeCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR014 - ResourceLevel - Path level should be smaller than non-suggested range", RuleType.BUG, Severity.MINOR, tags("resources"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("min-level", "4", RuleParamType.INTEGER);
        assertParameterProperties("max-level", "5", RuleParamType.INTEGER);
    }
}
