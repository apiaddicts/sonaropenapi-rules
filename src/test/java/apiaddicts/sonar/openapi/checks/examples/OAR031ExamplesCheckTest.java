package apiaddicts.sonar.openapi.checks.examples;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR031ExamplesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR031";
        check = new OAR031ExamplesCheck();
        v2Path = getV2Path("examples");
        v3Path = getV3Path("examples");
        v31Path = getV31Path("examples");
        v32Path = getV32Path("examples");
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
    public void verifyvalidV2ExternalRefs() {
        verifyV2("externalref.yaml");
    }

    @Test
    public void verifyInV2NestedProperties() {
        verifyV2("nested-properties-examples.yaml");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid.yaml");
    }
    @Test
    public void verifyInV31() {
        verifyV31("valid.yaml");
    }
    @Test
    public void verifyInV32() {
        verifyV32("valid.yaml");
    }

    @Test
    public void verifyInV3WithoutExamples() {
        verifyV3("without-examples.yaml");
    }
    @Test
    public void verifyInV31WithoutExamples() {
        verifyV31("without-examples.yaml");
    }
    @Test
    public void verifyInV32WithoutExamples() {
        verifyV32("without-examples.yaml");
    }

    @Test
    public void verifyvalidV3ExternalRefs() {
        verifyV3("externalref.yaml");
    }
    @Test
    public void verifyvalidV31ExternalRefs() {
        verifyV31("externalref.yaml");
    }
    @Test
    public void verifyvalidV32ExternalRefs() {
        verifyV32("externalref.yaml");
    }

    @Test
    public void verifyInV3NestedProperties() {
        verifyV3("nested-properties-examples.yaml");
    }
    @Test
    public void verifyInV31NestedProperties() {
        verifyV31("nested-properties-examples.yaml");
    }
    @Test
    public void verifyInV32NestedProperties() {
        verifyV32("nested-properties-examples.yaml");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR031 - Examples - Responses, Request Body, Parameters and Properties must have an example defined", RuleType.BUG, Severity.MAJOR, tags("examples"));
    }

}
