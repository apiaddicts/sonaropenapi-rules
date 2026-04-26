package apiaddicts.sonar.openapi.checks.schemas;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;
import apiaddicts.sonar.openapi.ExtendedOpenApiCheckVerifier;
import org.apiaddicts.apitools.dosonarapi.api.PreciseIssue;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OAR029StandardResponseSchemaCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR029";
        check = new OAR029StandardResponseSchemaCheck();
        v2Path = getV2Path("schemas");
        v3Path = getV3Path("schemas");
        v31Path = getV31Path("schemas");
        v32Path = getV32Path("schemas");
        v31Path = getV31Path("schemas");
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
    public void verifyInV2ValidAllOfMd() {
        verifyV2("valid-all-of-md");
    }

    @Test
    public void verifyInV2ValidMd() {
        verifyV2("valid-md");
    }


    @Test
    public void verifyInV2ValidR() {
        verifyV2("valid-r");
    }

    @Test
    public void verifyV2InvalidMissingRequired() {
        verifyV2("invalid-missing-required");
    }

    @Test
    public void verifyV2NoSchema() {
        verifyV2("no-schema");
    }

    @Test
    public void verifyV2Status204() {
        verifyV2("status-204");
    }

    @Test
    public void verifyV2DefaultResponse() {
        verifyV2("default-response");
    }

    @Test
    public void verifyV2ExcludedPath() {
        verifyV2("excluded-path");
    }

    @Test
    public void verifyV2EmptyProperties() {
        verifyV2("empty-properties");
    }

    @Test
    public void verifyV3MultipleContent() {
        verifyV3("multiple-content");
    }

    @Test
    public void verifyV3NoJsonContent() {
        verifyV3("no-json-content");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyInV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyInV32() {
        verifyV32("valid");
    }

    @Test
    public void verifyInV3AllOf() {
        verifyV3("valid-all-of");
    }
    @Test
    public void verifyInV31AllOf() {
        verifyV31("valid-all-of");
    }
    @Test
    public void verifyInV32AllOf() {
        verifyV32("valid-all-of");
    }

    @Test
    public void verifyInV3ValidAllOfMd() {
        verifyV3("valid-all-of-md");
    }
    @Test
    public void verifyInV31ValidAllOfMd() {
        verifyV31("valid-all-of-md");
    }
    @Test
    public void verifyInV32ValidAllOfMd() {
        verifyV32("valid-all-of-md");
    }

    @Test
    public void verifyInV3ValidMd() {
        verifyV3("valid-md");
    }
    @Test
    public void verifyInV31ValidMd() {
        verifyV31("valid-md");
    }
    @Test
    public void verifyInV32ValidMd() {
        verifyV32("valid-md");
    }

    @Test
    public void verifyInV3ValidR() {
        verifyV3("valid-r");
    }
    @Test
    public void verifyInV31ValidR() {
        verifyV31("valid-r");
    }
    @Test
    public void verifyInV32ValidR() {
        verifyV32("valid-r");
    }

    @Test
    public void verifyV31Valid() {
        verifyV31("valid.yaml");
    }

    @Test
    public void verifyV2WithArrayTypeValid() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"items\"],\"properties\":{\"items\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}}}}");
        verifyV2("array-type-valid.yaml");
    }

    @Test
    public void verifyV2WithArrayTypeMissingItems() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"items\"],\"properties\":{\"items\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}}}}");
        verifyV2("array-type-missing-items.yaml");
    }

    @Test
    public void verifyV2WithArrayTypeWrongItemsType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"items\"],\"properties\":{\"items\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}}}}");
        verifyV2("array-type-wrong-items-type.yaml");
    }

    @Test
    public void verifyV2WithArrayTypeRecursive() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"matrix\"],\"properties\":{\"matrix\":{\"type\":\"array\",\"items\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}}}");
        verifyV2("array-type-recursive.yaml");
    }

    @Test
    public void verifyV2WithArrayTypePrimitiveItems() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"tags\"],\"properties\":{\"tags\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}}}");
        verifyV2("array-type-primitive-items.yaml");
    }

    @Test
    public void verifyV2WithInvalidSchema() throws Exception {
        setResponseSchema("not valid json");
        List<PreciseIssue> issues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "valid.yaml"), check, true, false, false);
        assertThat(issues).isNotEmpty();
    }

    @Test
    public void verifyV2WithRequiredOnSuccessAndError() throws Exception {
        setResponseSchema("{\"requiredOnSuccess\":[\"status\"],\"requiredOnError\":[\"status\"]}");
        verifyV2("required-on-success-valid.yaml");
    }

    @Test
    public void verifyV2WithRequiredAlwaysAndDataProperty() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"object\"}}}");
        verifyV2("data-property.yaml");
    }

    @Test
    public void verifyV2WithEmptyDataProperty() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"object\"}}}");
        verifyV2("data-property-empty.yaml");
    }

    @Test
    public void verifyV2WithArrayParentDataProperty() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"object\"}}}");
        verifyV2("data-property-array-parent.yaml");
    }

    @Test
    public void verifyV2WithRootProperty() throws Exception {
        setResponseSchema("{\"rootProperty\":\"data\",\"requiredAlways\":[\"id\"],\"properties\":{\"id\":{\"type\":\"string\"}}}");
        verifyV2("root-property-valid.yaml");
    }

    @Test
    public void verifyV2WithRootPropertyEmptySub() throws Exception {
        setResponseSchema("{\"rootProperty\":\"data\",\"requiredAlways\":[\"id\"],\"properties\":{\"id\":{\"type\":\"string\"}}}");
        verifyV2("root-property-empty-sub.yaml");
    }

    @Test
    public void verifyV2WithRootPropertyWildcard() throws Exception {
        setResponseSchema("{\"rootProperty\":\"*\",\"requiredAlways\":[\"id\"],\"properties\":{\"id\":{\"type\":\"string\"}}}");
        verifyV2("root-wildcard-valid.yaml");
    }

    @Test
    public void verifyV2WithEmptyRequiredOnSuccess() throws Exception {
        setResponseSchema("{\"requiredOnSuccess\":[],\"requiredOnError\":[],\"requiredAlways\":[]}");
        verifyV2("required-on-success-valid.yaml");
    }

    @Test
    public void verifyV2WithNullDataPropertySchema() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\"}");
        verifyV2("data-property.yaml");
    }

    @Test
    public void verifyV2WithDataPropertySchemaNoType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{}}}");
        verifyV2("data-property.yaml");
    }

    @Test
    public void verifyV2WithDataPropertySchemaAnyType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"any\"}}}");
        verifyV2("data-property.yaml");
    }

    @Test
    public void verifyV2WithRequiredNameNotInProperties() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\",\"extra\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"object\"}}}");
        verifyV2("data-property-extra-required.yaml");
    }

    @Test
    public void verifyV2WithArrayParentDataPropertyEmpty() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"payload\"],\"dataProperty\":\"payload\",\"properties\":{\"payload\":{\"type\":\"object\"}}}");
        verifyV2("data-property-array-parent-empty.yaml");
    }

    @Test
    public void verifyV2WithPropertySchemaAnyType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"status\"],\"properties\":{\"status\":{\"type\":\"any\"}}}");
        verifyV2("status-any-type.yaml");
    }

    @Test
    public void verifyV2WithPropertySchemaNoType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"status\"],\"properties\":{\"status\":{\"properties\":{\"code\":{\"type\":\"integer\"}}}}}");
        verifyV2("status-schema-with-properties.yaml");
    }

    @Test
    public void verifyV2WithArrayItemsNoType() throws Exception {
        setResponseSchema("{\"requiredAlways\":[\"items\"],\"properties\":{\"items\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}}}}");
        verifyV2("array-items-no-type.yaml");
    }

    private void setResponseSchema(String schema) throws Exception {
        Field field = OAR029StandardResponseSchemaCheck.class.getDeclaredField("responseSchemaStr");
        field.setAccessible(true);
        field.set(check, schema);
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("response-schema", "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"code\"]},\"payload\":{\"type\":\"any\"}},\"required\":[\"status\",\"payload\"]}", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR029 - StandardResponse - Response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("schemas"));
    }
}
