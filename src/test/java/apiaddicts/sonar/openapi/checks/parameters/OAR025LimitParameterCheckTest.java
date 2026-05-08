package apiaddicts.sonar.openapi.checks.parameters;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR025LimitParameterCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR025";
        check = new OAR025LimitParameterCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
        v31Path = getV31Path("parameters");
        v32Path = getV32Path("parameters");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2Excluded() {
        verifyV2("excluded");
    }

    @Test
    public void verifyInV2Without() {
        verifyV2("plain-without");
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
    public void verifyInV3Excluded() {
        verifyV3("excluded");
    }
    @Test
    public void verifyInV31Excluded() {
        verifyV31("excluded");
    }
    @Test
    public void verifyInV32Excluded() {
        verifyV32("excluded");
    }

    @Test
    public void verifyInV3Without() {
        verifyV3("plain-without");
    }
    @Test
    public void verifyInV31Without() {
        verifyV31("plain-without");
    }
    @Test
    public void verifyInV32Without() {
        verifyV32("plain-without");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR025 - LimitParameter - the chosen parameter must be defined in this operation", RuleType.BUG, Severity.MAJOR, tags("parameters"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("paths", "/examples", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/include", RuleParamType.STRING);
    }
}