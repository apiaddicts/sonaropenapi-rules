package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR017ResourcePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR017";
        check = new OAR017ResourcePathCheck();
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
        assertRuleProperties("OAR017 - ResourcePath - Resource path should alternate static and parametrized parts", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

}