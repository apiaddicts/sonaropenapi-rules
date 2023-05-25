package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR072NonOKModelResponseCheckTest extends BaseCheckTest {
    
    @Before
    public void init() {
        ruleName = "OAR072";
        check = new OAR072NonOKModelResponseCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2WithStackTrace() {
        verifyV2("with-stack-trace");
    }

    @Test
    public void verifyInV2NoStackTrace() {
        verifyV2("no-stack-trace");
    }

    @Test
    public void verifyInV3Compliant() {
        verifyV3("with-stack-trace");
    }

    @Test
    public void verifyInV3WithStackTrace() {
        verifyV3("no-stack-trace");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR072 - NonOKModelResponseCheck - Non OK responses shouldnt have stackTraces", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }
}