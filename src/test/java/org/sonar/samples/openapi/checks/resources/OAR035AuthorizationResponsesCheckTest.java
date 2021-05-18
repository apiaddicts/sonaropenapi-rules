package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR035AuthorizationResponsesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR035";
        check = new OAR035AuthorizationResponsesCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithoutAuthorizationResponses() {
        verifyV2("without-authorization-responses");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3WithoutAuthorizationResponses() {
        verifyV3("without-authorization-responses");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR035 - AuthorizationResponses - Responses in a specification with security schemes defined, must define authorization response codes", RuleType.VULNERABILITY, Severity.MAJOR, tags("resources"));
    }
}