package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR010DefaultResponseMediaTypeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR010";
        check = new OAR010DefaultResponseMediaTypeCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2WithDefault() {
        verifyV2("with-default");
    }

    @Test
    public void verifyInV2WithWrongDefault() {
        verifyV2("with-wrong-default");
    }

    @Test
    public void verifyInV2WithSpecific() {
        verifyV2("with-specific");
    }

    @Test
    public void verifyInV2WithDefaultAndSpecific() {
        verifyV2("with-default-and-specific");
    }

    @Test
    public void verifyInV2WithWrongDefaultAndSpecific() {
        verifyV2("with-wrong-default-and-specific");
    }

    @Test
    public void verifyInV2WithoutAnything() {
        verifyV2("without-anything");
    }

    @Test
    public void verifyInV3WithDefault() {
        verifyV3("with-default-and-$ref");
    }
    @Test
    public void verifyInV31WithDefault() {
        verifyV31("with-default-and-$ref");
    }
    @Test
    public void verifyInV32WithDefault() {
        verifyV32("with-default-and-$ref");
    }

    @Test
    public void verifyInV3WithWrongDefault() {
        verifyV3("with-wrong-default");
    }
    @Test
    public void verifyInV31WithWrongDefault() {
        verifyV31("with-wrong-default");
    }
    @Test
    public void verifyInV32WithWrongDefault() {
        verifyV32("with-wrong-default");
    }

    @Test
    public void verifyInV3WithSpecific() {
        verifyV3("with-specific");
    }
    @Test
    public void verifyInV31WithSpecific() {
        verifyV31("with-specific");
    }
    @Test
    public void verifyInV32WithSpecific() {
        verifyV32("with-specific");
    }

    @Test
    public void verifyInV3WithWrongDefaultAndSpecific() {
        verifyV3("with-wrong-default-and-specific");
    }
    @Test
    public void verifyInV31WithWrongDefaultAndSpecific() {
        verifyV31("with-wrong-default-and-specific");
    }
    @Test
    public void verifyInV32WithWrongDefaultAndSpecific() {
        verifyV32("with-wrong-default-and-specific");
    }

    @Test
    public void verifyInV3WithDefaultAndSpecific() {
        verifyV3("with-default-and-specific");
    }
    @Test
    public void verifyInV31WithDefaultAndSpecific() {
        verifyV31("with-default-and-specific");
    }
    @Test
    public void verifyInV32WithDefaultAndSpecific() {
        verifyV32("with-default-and-specific");
    }

    @Test
    public void verifyInV3WithoutAnything() {
        verifyV3("without-anything");
    }
    @Test
    public void verifyInV31WithoutAnything() {
        verifyV31("without-anything");
    }
    @Test
    public void verifyInV32WithoutAnything() {
        verifyV32("without-anything");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR010 - DefaultResponseMediaType - Should indicate the default response media type", RuleType.BUG, Severity.MINOR, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("media-type-exceptions", "-", RuleParamType.STRING);
        assertParameterProperties("default-media-type", "application/json", RuleParamType.STRING);
    }
}
