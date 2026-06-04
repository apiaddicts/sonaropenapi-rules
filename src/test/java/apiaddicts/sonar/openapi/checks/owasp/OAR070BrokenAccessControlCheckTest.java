package apiaddicts.sonar.openapi.checks.owasp;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR070BrokenAccessControlCheckTest extends BaseCheckTest{

    @Before
    public void init() {
        ruleName = "OAR070";
        check = new OAR070BrokenAccessControlCheck();
        v2Path = getV2Path("owasp");
        v3Path = getV3Path("owasp");
        v31Path = getV31Path("owasp");
        v32Path = getV32Path("owasp");
    }

    @Test
    public void verifyInV2numeric() {
        verifyV2("numeric");
    }
    @Test
    public void verifyInV2noNumeric() {
        verifyV2("no-numeric");
    }
    @Test
    public void verifyInV3numeric() {
        verifyV3("numeric");
    }
    @Test
    public void verifyInV31numeric() {
        verifyV31("numeric");
    }
    @Test
    public void verifyInV32numeric() {
        verifyV32("numeric");
    }
    @Test
    public void verifyInV3noNumeric() {
        verifyV3("no-numeric");
    }
    @Test
    public void verifyInV31noNumeric() {
        verifyV31("no-numeric");
    }
    @Test
    public void verifyInV32noNumeric() {
        verifyV32("no-numeric");
    }
    @Override
    public void verifyRule() {
        assertRuleProperties("OAR070 - BrokenAccessControl - Parameters in path shouldn't be numeric", RuleType.VULNERABILITY, Severity.MAJOR, tags("owasp"));
    }
    
}
