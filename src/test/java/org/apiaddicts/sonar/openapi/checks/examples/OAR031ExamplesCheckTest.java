package org.apiaddicts.sonar.openapi.checks.examples;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.examples.OAR031ExamplesCheck;

public class OAR031ExamplesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR031";
        check = new OAR031ExamplesCheck();
        v2Path = getV2Path("examples");
        v3Path = getV3Path("examples");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid.yaml");
    }

    @Test
    public void verifyInV2WithoutExamples() {
        verifyV2("without-examples.yaml");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid.yaml");
    }

    @Test
    public void verifyInV3WithoutExamples() {
        verifyV3("without-examples.yaml");
    }

    @Test
    public void verifyvalidV3ExternalRefs() {
        verifyV3("externalref.yaml");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR031 - Examples - Responses, Request Body, Parameters and Properties must have an example defined", RuleType.BUG, Severity.MAJOR, tags("examples"));
    }

}
