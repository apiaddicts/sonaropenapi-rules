package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR029StandardResponseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR029";
        check = new OAR029StandardResponseCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    /*@Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2AllOf() {
        verifyV2("valid-all-of");
    }

    @Test
    public void verifyInV2WithoutData() {
        verifyV2("without-data");
    }

    @Test
    public void verifyInV2WithoutResult() {
        verifyV2("without-result");
    }

    @Test
    public void verifyInV2WithResultWrongType() {
        verifyV2("with-result-wrong-type");
    }

    @Test
    public void verifyInV2WithResultWithoutProperties() {
        verifyV2("with-result-without-properties");
    }

    @Test
    public void verifyInV2WithResultWithPropertiesWrongType() {
        verifyV2("with-result-with-properties-wrong-type");
    }

    @Test
    public void verifyInV2WithoutRequiredFields() {
        verifyV2("without-required-fields");
    }*/

    public void verifyInV2() {
        verifyV2("valid_r");
    }

    @Test
    public void verifyInV2WithoutPayload() {
        verifyV2("without-payload");
    }

    @Test
    public void verifyInV2WithoutError() {
        verifyV2("without-error");
    }

    @Test
    public void verifyInV2WithErrorWrongType() {
        verifyV2("with-error-wrong-type");
    }

    @Test
    public void verifyInV2WithErrorWithoutProperties() {
        verifyV2("with-error-without-properties");
    }

    @Test
    public void verifyInV2WithErrorWithPropertiesWrongType() {
        verifyV2("with-error-with-properties-wrong-type");
    }

    @Test
    public void verifyInV2WithoutRequiredFields() {
        verifyV2("without-required-fields_r");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3WithoutPayload() {
        verifyV3("without-payload");
    }

    @Test
    public void verifyInV3WithoutError() {
        verifyV3("without-error");
    }

    @Test
    public void verifyInV3WithErrorWrongType() {
        verifyV3("with-error-wrong-type");
    }

    @Test
    public void verifyInV3WithErrorWithoutProperties() {
        verifyV3("with-error-without-properties");
    }

    @Test
    public void verifyInV3WithErrorWithPropertiesWrongType() {
        verifyV3("with-error-with-properties-wrong-type");
    }

    @Test
    public void verifyInV3WithoutRequiredFields() {
        verifyV3("without-required-fields");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"error\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"string\"},\"message\":{\"type\":\"string\"},\"httpStatus\":{\"type\":\"integer\"},\"details\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"code\",\"message\",\"httpStatus\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnError\":[\"error\"],\"requiredOnSuccess\":[\"payload\"],\"dataProperty\":\"payload\"}", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR029 - StandardResponse - Response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("resources"));
    }
}
