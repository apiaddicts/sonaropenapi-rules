package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR091ParamOnlyRefCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR091";
        check = new OAR091ParamOnlyRefCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2noref() {
        verifyV2("no-ref");
    }

    @Test
    public void verifyInV2withref() {
        verifyV2("with-ref");
    }

    @Test
    public void verifyInV3noref() {
        verifyV3("no-ref");
    }

    @Test
    public void verifyInV3withref() {
        verifyV3("with-ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR091 - ParamOnlyRef - Parameters must contain only references ($ref)", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }
}