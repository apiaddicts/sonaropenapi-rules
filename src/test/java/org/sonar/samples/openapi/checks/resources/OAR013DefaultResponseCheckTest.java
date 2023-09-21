package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.resources.OAR013DefaultResponseCheck;

public class OAR013DefaultResponseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR013";
        check = new OAR013DefaultResponseCheck();
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
        assertRuleProperties("OAR013 - DefaultResponse - Default response is required", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }
}
