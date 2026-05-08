package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR078VerbsSecurityCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR078";
        check = new OAR078VerbsSecurityCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2WithSecurity() {
        verifyV2("with-security");
    }

    @Test
    public void verifyInV2NoSecurity() {
        verifyV2("no-security");
    }

    @Test
    public void verifyInV3WithSecurity() {
        verifyV3("with-security");
    }
    @Test
    public void verifyInV31WithSecurity() {
        verifyV31("with-security");
    }
    @Test
    public void verifyInV32WithSecurity() {
        verifyV32("with-security");
    }

    @Test
    public void verifyInV3NoSecurity() {
        verifyV3("no-security");
    }
    @Test
    public void verifyInV31NoSecurity() {
        verifyV31("no-security");
    }
    @Test
    public void verifyInV32NoSecurity() {
        verifyV32("no-security");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR078 - VerbsSecurity - All API methods must have security", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
}