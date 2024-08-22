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
        assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"http_status\":{\"type\":\"string\"},\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"http_status\",\"code\",\"description\",\"errors\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnSuccess\":[\"data\"],\"requiredAlways\":[\"status\"],\"dataProperty\":\"data\"}", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR029 - StandardResponse - Response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("resources"));
    }
}
