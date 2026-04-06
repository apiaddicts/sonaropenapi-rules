package apiaddicts.sonar.openapi.checks.regex;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

import java.lang.reflect.Field;

public class OAR112RegexCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR112";
        check = new OAR112RegexCheck();
        v2Path = getV2Path("regex");
        v3Path = getV3Path("regex");
    }

    private void setField(String fieldName, String value) {
        try {
            Field field = OAR112RegexCheck.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(check, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Test
    public void verifyInfoDescriptionInvalidInV2() {
        verifyV2("info-invalid");
    }

    @Test
    public void verifyInfoDescriptionInvalidInV3() {
        verifyV3("info-invalid");
    }

    @Test
    public void verifyServersDescriptionValidInV3() {
        setField("nodes", "servers/description");
        verifyV3("servers-valid");
    }

    @Test
    public void verifyServersDescriptionInvalidInV3() {
        setField("nodes", "servers/description");
        verifyV3("servers-invalid");
    }

    @Test
    public void verifyOperationSummaryValidInV3() {
        setField("nodes", "paths/get/summary");
        verifyV3("operation-valid");
    }

    @Test
    public void verifyOperationSummaryInvalidInV3() {
        setField("nodes", "paths/get/summary");
        verifyV3("operation-invalid");
    }

    @Test
    public void verifyOperationSummaryValidInV2() {
        setField("nodes", "paths/get/summary");
        verifyV2("operation-valid");
    }

    @Test
    public void verifyOperationSummaryInvalidInV2() {
        setField("nodes", "paths/get/summary");
        verifyV2("operation-invalid");
    }

    @Test
    public void verifyTagsNameValidInV3() {
        setField("nodes", "tags/name");
        verifyV3("tags-valid");
    }

    @Test
    public void verifyTagsNameInvalidInV3() {
        setField("nodes", "tags/name");
        verifyV3("tags-invalid");
    }

    @Test
    public void verifyTagsNameValidInV2() {
        setField("nodes", "tags/name");
        verifyV2("tags-valid");
    }

    @Test
    public void verifyTagsNameInvalidInV2() {
        setField("nodes", "tags/name");
        verifyV2("tags-invalid");
    }

    @Test
    public void verifyExternalDocsDescriptionValidInV3() {
        setField("nodes", "externalDocs/description");
        verifyV3("external-docs-valid");
    }

    @Test
    public void verifyExternalDocsDescriptionInvalidInV3() {
        setField("nodes", "externalDocs/description");
        verifyV3("external-docs-invalid");
    }

    @Test
    public void verifyExternalDocsDescriptionValidInV2() {
        setField("nodes", "externalDocs/description");
        verifyV2("external-docs-valid");
    }

    @Test
    public void verifyExternalDocsDescriptionInvalidInV2() {
        setField("nodes", "externalDocs/description");
        verifyV2("external-docs-invalid");
    }

    @Test
    public void verifyParametersDescriptionValidInV3() {
        setField("nodes", "paths/get/parameters/description");
        verifyV3("parameters-valid");
    }

    @Test
    public void verifyParametersDescriptionInvalidInV3() {
        setField("nodes", "paths/get/parameters/description");
        verifyV3("parameters-invalid");
    }

    @Test
    public void verifyBooleanTrueMissingFieldInV3() {
        setField("nodes", "info/description");
        setField("valid", "true");
        verifyV3("missing-description");

        setField("valid", "false");
        verifyV3("info-invalid");
        verifyV3("minimal");
    }

    @Test
    public void verifyBooleanTrueMissingFieldInV2() {
        setField("nodes", "info/description");
        setField("valid", "true");
        verifyV2("missing-description");

        setField("valid", "false");
        verifyV2("info-invalid");
        verifyV2("minimal");
    }

    @Test
    public void verifyBooleanFalseFieldPresentInV3() {
        setField("nodes", "info/description");
        setField("valid", "false");
        verifyV3("info-invalid");
    }

    @Test
    public void verifyBooleanFalseFieldPresentInV2() {
        setField("nodes", "info/description");
        setField("valid", "false");
        verifyV2("info-invalid");
    }

    @Test
    public void verifyBooleanFalseFieldAbsentInV3() {
        setField("nodes", "info/description");
        setField("valid", "false");
        verifyV3("minimal");
    }

    @Test
    public void verifyBooleanFalseFieldAbsentInV2() {
        setField("nodes", "info/description");
        setField("valid", "false");
        verifyV2("minimal");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR112 - TemplateRule - Create your own OpenAPI rules", RuleType.BUG, Severity.MINOR, tags("regex"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("Node", "info/description", RuleParamType.STRING);
        assertParameterProperties("Error Message", "The field must start with an uppercase letter.", RuleParamType.STRING);
        assertParameterProperties("Validation", "^[A-Z].*", RuleParamType.STRING);
    }
}