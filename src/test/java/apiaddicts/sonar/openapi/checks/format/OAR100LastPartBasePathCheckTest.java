package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR100LastPartBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR100";
        check = new OAR100LastPartBasePathCheck();
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
    public void verifyInV2IncorrectVersion() {
        verifyV2("incorrect-version");
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
    public void verifyInV3IncorrectVersion() {
        verifyV3("incorrect-version");
    }
    @Test
    public void verifyInV31IncorrectVersion() {
        verifyV31("incorrect-version");
    }
    @Test
    public void verifyInV32IncorrectVersion() {
        verifyV32("incorrect-version");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR100 - LastPartBasePath - The last part of the path should be the API version", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(0);
    }
}