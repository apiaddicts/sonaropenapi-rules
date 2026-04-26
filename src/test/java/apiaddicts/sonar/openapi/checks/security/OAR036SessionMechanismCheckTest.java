package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR036SessionMechanismCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR036";
        check = new OAR036SessionMechanismsCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithForbiddenParams() {
        verifyV2("with-cookie");
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
    public void verifyInV3WithForbiddenParams() {
        verifyV3("with-cookie");
    }
    @Test
    public void verifyInV31WithForbiddenParams() {
        verifyV31("with-cookie");
    }
    @Test
    public void verifyInV32WithForbiddenParams() {
        verifyV32("with-cookie");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR036 - SessionMechanismCheck - Session mechanisms are forbidden", RuleType.VULNERABILITY, Severity.BLOCKER, tags("safety"));
    }
}
