package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR097ShortBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR097";
        check = new OAR097ShortBasePathCheck();
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
    public void verifyInV2TooShort() {
        verifyV2("too-short");
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
    public void verifyInV3TooShort() {
        verifyV3("too-short");
    }
    @Test
    public void verifyInV31TooShort() {
        verifyV31("too-short");
    }
    @Test
    public void verifyInV32TooShort() {
        verifyV32("too-short");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR097 - ShortBasePath - Base path must have at least two parts", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(0);
    }
}