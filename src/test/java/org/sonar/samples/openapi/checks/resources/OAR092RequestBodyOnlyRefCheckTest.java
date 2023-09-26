package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.resources.OAR092RequestBodyOnlyRefCheck;


public class OAR092RequestBodyOnlyRefCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR092";
        check = new OAR092RequestBodyOnlyRefCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
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
        assertRuleProperties("OAR092 - RequestBodyOnlyRef - RequestBody must contain only references ($ref)", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }
}