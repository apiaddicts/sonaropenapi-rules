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
    
    //MD
    /*@Test
    public void verifyInV2() {
        verifyV2("valid-md");
    }

    @Test
    public void verifyInV2AllOf() {
        verifyV2("valid-all-of-md");
    }

    @Test
    public void verifyInV2WithoutData() {
        verifyV2("without-data-md");
    }

    @Test
    public void verifyInV2WithoutResult() {
        verifyV2("without-result-md");
    }

    @Test
    public void verifyInV2WithResultWrongType() {
        verifyV2("with-result-wrong-type-md");
    }

    @Test
    public void verifyInV2WithResultWithoutProperties() {
        verifyV2("with-result-without-properties-md");
    }

    @Test
    public void verifyInV2WithResultWithPropertiesWrongType() {
        verifyV2("with-result-with-properties-wrong-type-md");
    }

    @Test
    public void verifyInV2WithoutRequiredFields() {
        verifyV2("without-required-fields-md");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid-md");
    }

    @Test
    public void verifyInV3AllOf() {
        verifyV3("valid-all-of-md");
    }

    @Test
    public void verifyInV3WithoutData() {
        verifyV3("without-data-md");
    }

    @Test
    public void verifyInV3WithoutResult() {
        verifyV3("without-result-md.yaml");
    }

    @Test
    public void verifyInV3WithResultWrongType() {
        verifyV3("with-result-wrong-type-md");
    }

    @Test
    public void verifyInV3WithResultWithoutProperties() {
        verifyV3("with-result-without-properties-md");
    }

    @Test
    public void verifyInV3WithResultWithPropertiesWrongType() {
        verifyV3("with-result-with-properties-wrong-type-md");
    }

    @Test
    public void verifyInV3WithoutRequiredFields() {
        verifyV3("without-required-fields-md");
    }*/

    //RIM
    /*@Test
    public void verifyInV2() {
        verifyV2("valid-r");
    }

    @Test
    public void verifyInV2WithoutPayload() {
        verifyV2("without-payload-r");
    }

    @Test
    public void verifyInV2WithoutError() {
        verifyV2("without-error-r");
    }

    @Test
    public void verifyInV2WithErrorWrongType() {
        verifyV2("with-error-wrong-type-r");
    }

    @Test
    public void verifyInV2WithErrorWithoutProperties() {
        verifyV2("with-error-without-properties-r");
    }

    @Test
    public void verifyInV2WithErrorWithPropertiesWrongType() {
        verifyV2("with-error-with-properties-wrong-type-r");
    }

    @Test
    public void verifyInV2WithoutRequiredFields() {
        verifyV2("without-required-fields-r");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid-r");
    }

    @Test
    public void verifyInV3WithoutPayload() {
        verifyV3("without-payload-r");
    }

    @Test
    public void verifyInV3WithoutError() {
        verifyV3("without-error-r");
    }

    @Test
    public void verifyInV3WithErrorWrongType() {
        verifyV3("with-error-wrong-type-r");
    }

    @Test
    public void verifyInV3WithErrorWithoutProperties() {
        verifyV3("with-error-without-properties-r.yaml");
    }

    @Test
    public void verifyInV3WithErrorWithPropertiesWrongType() {
        verifyV3("with-error-with-properties-wrong-type-r");
    }

    @Test
    public void verifyInV3WithoutRequiredFields() {
        verifyV3("without-required-fields-r");
    }*/

    //CloudAppi
    @Test
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
    public void verifyInV2WithoutStatus() {
        verifyV2("without-status");
    }

    @Test
    public void verifyInV2WithStatusWrongType() {
        verifyV2("with-status-wrong-type");
    }

    @Test
    public void verifyInV2WithStatusWithoutProperties() {
        verifyV2("with-status-without-properties");
    }

    @Test
    public void verifyInV2WithStatusWithPropertiesWrongType() {
        verifyV2("with-status-with-properties-wrong-type");
    }

    @Test
    public void verifyInV2WithoutRequiredFields() {
        verifyV2("without-required-fields");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3AllOf() {
        verifyV3("valid-all-of");
    }

    @Test
    public void verifyInV3WithoutData() {
        verifyV3("without-data");
    }

    @Test
    public void verifyInV3WithoutStatus() {
        verifyV3("without-status");
    }

    @Test
    public void verifyInV3WithStatusWrongType() {
        verifyV3("with-status-wrong-type");
    }

    @Test
    public void verifyInV3WithStatusWithoutProperties() {
        verifyV3("with-status-without-properties");
    }

    @Test
    public void verifyInV3WithStatusWithPropertiesWrongType() {
        verifyV3("with-status-with-properties-wrong-type");
    }

    @Test
    public void verifyInV3WithoutRequiredFields() {
        verifyV3("without-required-fields");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        //MD
        //assertParameterProperties("response-schema", "{\"type\": \"object\",\"properties\": {\"result\": {\"type\": \"object\",\"properties\": {\"http_code\": {\"type\": \"integer\"},\"status\": {\"type\": \"boolean\"},\"trace_id\": {\"type\": \"string\"},\"errors\": {\"type\": \"array\",\"items\": {\"type\": \"object\",\"properties\": {\"code\": {\"type\": \"integer\"},\"message\": {\"type\": \"string\"}},\"required\": [\"code\",\"message\"]}}},\"required\": [\"status\",\"http_code\",\"trace_id\"]},\"data\": {\"type\": \"any\"}},\"requiredOnError\": [],\"requiredOnSuccess\": [\"data\"],\"requiredAlways\": [\"result\"],\"dataProperty\": \"data\"}", RuleParamType.STRING);
        //RIM
        //assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"error\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"string\"},\"message\":{\"type\":\"string\"},\"httpStatus\":{\"type\":\"integer\"},\"details\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"code\",\"message\",\"httpStatus\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnError\":[\"error\"],\"requiredOnSuccess\":[\"payload\"],\"dataProperty\":\"payload\"}", RuleParamType.STRING);
        //Cloudappi
        assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"http_status\":{\"type\":\"string\"},\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"http_status\",\"code\",\"description\",\"errors\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnSuccess\":[\"data\"],\"requiredAlways\":[\"status\"],\"dataProperty\":\"data\"}", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR029 - StandardResponse - Response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("resources"));
    }
}
