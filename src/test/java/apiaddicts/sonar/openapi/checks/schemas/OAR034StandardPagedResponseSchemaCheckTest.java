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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OAR034StandardPagedResponseSchemaCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR034";
        check = new OAR034StandardPagedResponseSchemaCheck();
        v2Path = getV2Path("schemas");
        v3Path = getV3Path("schemas");
        v31Path = getV31Path("schemas");
        v32Path = getV32Path("schemas");
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
    public void verifyInV2WithoutPaging() {
        verifyV2("without-paging");
    }

    @Test
    public void verifyInV2WithPagingWrongType() {
        verifyV2("with-paging-wrong-type");
    }

    @Test
    public void verifyInV2WithPagingWithoutProperties() {
        verifyV2("with-paging-without-properties");
    }

    @Test
    public void verifyInV2WithPagingWithPropertiesWrongTypes() {
        verifyV2("with-paging-with-properties-wrong-types");
    }

    @Test
    public void verifyInV2WithLinksWithoutProperties() {
        verifyV2("with-links-without-properties");
    }

    @Test
    public void verifyInV2WithoutLinksRequiredFields() {
        verifyV2("without-links-required-fields");
    }

    @Test
    public void verifyInV2WithoutPaginationRequiredFields() {
        verifyV2("without-pagination-required-fields");
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
    public void verifyInV3WithoutPaging() {
        verifyV3("without-paging");
    }
    @Test
    public void verifyInV31WithoutPaging() {
        verifyV31("without-paging");
    }
    @Test
    public void verifyInV32WithoutPaging() {
        verifyV32("without-paging");
    }

    @Test
    public void verifyInV3WithPagingWrongType() {
        verifyV3("with-paging-wrong-type");
    }
    @Test
    public void verifyInV31WithPagingWrongType() {
        verifyV31("with-paging-wrong-type");
    }
    @Test
    public void verifyInV32WithPagingWrongType() {
        verifyV32("with-paging-wrong-type");
    }

    @Test
    public void verifyInV3WithPagingWithoutProperties() {
        verifyV3("with-paging-without-properties");
    }
    @Test
    public void verifyInV31WithPagingWithoutProperties() {
        verifyV31("with-paging-without-properties");
    }
    @Test
    public void verifyInV32WithPagingWithoutProperties() {
        verifyV32("with-paging-without-properties");
    }

    @Test
    public void verifyInV3WithPagingWithPropertiesWrongTypes() {
        verifyV3("with-paging-with-properties-wrong-types");
    }
    @Test
    public void verifyInV31WithPagingWithPropertiesWrongTypes() {
        verifyV31("with-paging-with-properties-wrong-types");
    }
    @Test
    public void verifyInV32WithPagingWithPropertiesWrongTypes() {
        verifyV32("with-paging-with-properties-wrong-types");
    }

    @Test
    public void verifyInV3WithLinksWithoutProperties() {
        verifyV3("with-links-without-properties");
    }
    @Test
    public void verifyInV31WithLinksWithoutProperties() {
        verifyV31("with-links-without-properties");
    }
    @Test
    public void verifyInV32WithLinksWithoutProperties() {
        verifyV32("with-links-without-properties");
    }

    @Test
    public void verifyInV3WithoutLinksRequiredFields() {
        verifyV3("without-links-required-fields");
    }
    @Test
    public void verifyInV31WithoutLinksRequiredFields() {
        verifyV31("without-links-required-fields");
    }
    @Test
    public void verifyInV32WithoutLinksRequiredFields() {
        verifyV32("without-links-required-fields");
    }

    @Test
    public void verifyInV3WithoutPaginationRequiredFields() {
        verifyV3("without-pagination-required-fields");
    }
    @Test
    public void verifyInV31WithoutPaginationRequiredFields() {
        verifyV31("without-pagination-required-fields");
    }
    @Test
    public void verifyInV32WithoutPaginationRequiredFields() {
        verifyV32("without-pagination-required-fields");
    }

    @Test
    public void verifyInV2WithPagingNoType() {
        List<PreciseIssue> issues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "with-paging-no-type.yaml"), check, true, false, false, false);
        assertThat(issues).isNotEmpty();
    }

    @Test
    public void verifyInV2WithPagingAllofWrongType() {
        verifyV2("with-paging-allof-wrong-type.yaml");
    }

    @Test
    public void verifyInV2WithoutPagingRequiredKey() {
        List<PreciseIssue> issues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "without-paging-required-key.yaml"), check, true, false, false);
        assertThat(issues).isNotEmpty();
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("paging-schema", "{\"type\":\"object\",\"properties\":{\"numPages\":{\"type\":\"integer\"},\"total\":{\"type\":\"integer\"},\"start\":{\"type\":\"integer\"},\"limit\":{\"type\":\"integer\"},\"links\":{\"type\":\"object\",\"properties\":{\"next\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"previous\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"last\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"self\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"first\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}}},\"required\":[\"self\",\"previous\",\"next\"]}},\"required\":[\"start\",\"limit\",\"links\"],\"pagingPropertyName\":\"paging\"}", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR034 - StandardPagedResponse - Paged response schema must be compliant with the standard", RuleType.BUG, Severity.BLOCKER, tags("schemas"));
    }
}
