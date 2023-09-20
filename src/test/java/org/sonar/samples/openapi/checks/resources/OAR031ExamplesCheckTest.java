package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.resources.OAR031ExamplesCheck;

public class OAR031ExamplesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR031";
        check = new OAR031ExamplesCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithoutExamples() {
        verifyV2("without-examples");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3WithoutExamples() {
        verifyV3("without-examples");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR031 - Examples - Responses, Request Body, Parameters and Properties must have an example defined", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

}
