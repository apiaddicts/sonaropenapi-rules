package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR068PascalCaseNamingConventionCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR068";
        check = new OAR068PascalCaseNamingConventionCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("pascal-case-error");
    }
    @Test
    public void verifyvalidV2() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3() {
        verifyV3("pascal-case-error");
    }
    @Test
    public void verifyInV31() {
        verifyV31("pascal-case-error");
    }
    @Test
    public void verifyInV32() {
        verifyV32("pascal-case-error");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyvalidV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyvalidV32() {
        verifyV32("valid");
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

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR068 - PascalCaseNamingConvention - RequestBody and Responses schema property names must be compliant with the PascalCase naming convention", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
