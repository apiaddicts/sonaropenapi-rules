package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR081PasswordFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR081";
        check = new OAR081PasswordFormatCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2notvalid() {
        verifyV2("not-valid-password");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-password");
    }

    @Test
    public void verifyInV3notvalid() {
        verifyV3("not-valid-password");
    }
    @Test
    public void verifyInV31notvalid() {
        verifyV31("not-valid-password");
    }
    @Test
    public void verifyInV32notvalid() {
        verifyV32("not-valid-password");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-password");
    }
    @Test
    public void verifyvalidV31() {
        verifyV31("valid-password");
    }
    @Test
    public void verifyvalidV32() {
        verifyV32("valid-password");
    }
    @Test
    public void verifyInV2Components() {
        verifyV2("valid-with-components");
    }

    @Test
    public void verifyInV3Components() {
        verifyV3("valid-with-components");
    }
    @Test
    public void verifyInV31Components() {
        verifyV31("valid-with-components");
    }
    @Test
    public void verifyInV32Components() {
        verifyV32("valid-with-components");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR081 - PasswordFormat - Fields of type password should be string with format password", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
}
