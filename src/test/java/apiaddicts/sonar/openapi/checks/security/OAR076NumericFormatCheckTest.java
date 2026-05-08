package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR076NumericFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR076";
        check = new OAR076NumericFormatCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2WithNested() {
        verifyV2("nested");
    }

    @Test
    public void verifyInV2With$ref() {
        verifyV2("with-$ref");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }
    @Test
    public void verifyInV31() {
        verifyV31("plain");
    }
    @Test
    public void verifyInV32() {
        verifyV32("plain");
    }

    @Test
    public void verifyInV3WithNested() {
        verifyV3("nested");
    }
    @Test
    public void verifyInV31WithNested() {
        verifyV31("nested");
    }
    @Test
    public void verifyInV32WithNested() {
        verifyV32("nested");
    }

    @Test
    public void verifyInV3With$ref() {
        verifyV3("with-$ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR076 - NumericFormat - Numeric types requires a valid format", RuleType.VULNERABILITY, Severity.MINOR, tags("safety"));
    }
}