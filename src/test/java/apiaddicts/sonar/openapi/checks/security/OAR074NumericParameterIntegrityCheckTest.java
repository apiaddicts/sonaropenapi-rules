package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR074NumericParameterIntegrityCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR074";
        check = new OAR074NumericParameterIntegrityCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2withRestrictions() {
        verifyV2("with-restrictions");
    }

    @Test
    public void verifyInV2noRestrictions() {
        verifyV2("no-restrictions");
    }

    @Test
    public void verifyInV3withRestrictions() {
        verifyV3("with-restrictions");
    }
    @Test
    public void verifyInV31withRestrictions() {
        verifyV31("with-restrictions");
    }
    @Test
    public void verifyInV32withRestrictions() {
        verifyV32("with-restrictions");
    }

    @Test
    public void verifyInV3noRestrictions() {
        verifyV3("no-restrictions");
    }
    @Test
    public void verifyInV31noRestrictions() {
        verifyV31("no-restrictions");
    }
    @Test
    public void verifyInV32noRestrictions() {
        verifyV32("no-restrictions");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR074 - NumericParameterIntegrityCheck - Numeric parameters should have minimum, maximum, or format restriction", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
}