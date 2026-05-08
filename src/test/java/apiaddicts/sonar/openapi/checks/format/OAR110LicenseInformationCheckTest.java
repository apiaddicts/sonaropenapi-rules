package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR110LicenseInformationCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR110";
        check = new OAR110LicenseInformationCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
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


    @Override
    public void verifyRule() {
        assertRuleProperties("OAR110 - LicenseInformation - License information cannot be empty", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

}