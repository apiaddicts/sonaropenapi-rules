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
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"result\":{\"type\":\"object\",\"properties\":{\"http_code\":{\"type\":\"integer\"},\"status\":{\"type\":\"boolean\"},\"trace_id\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"name\",\"value\"]}}},\"required\":[\"status\",\"http_code\",\"trace_id\"]},\"data\":{\"type\":\"any\"}},\"requiredOnError\":[],\"requiredOnSuccess\":[\"data\"],\"requiredAlways\":[\"result\"],\"dataProperty\":\"data\"}", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR029 - StandardResponse - Response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("resources"));
    }
}
