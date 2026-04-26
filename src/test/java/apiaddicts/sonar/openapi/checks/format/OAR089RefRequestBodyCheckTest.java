package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR089RefRequestBodyCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR089";
        check = new OAR089RefRequestBodyCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV3() {
        verifyV3("invalid-ref");
    }
    @Test
    public void verifyInV31() {
        verifyV31("invalid-ref");
    }
    @Test
    public void verifyInV32() {
        verifyV32("invalid-ref");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-ref");
    }
    @Test
    public void verifyvalidV31() {
        verifyV31("valid-ref");
    }
    @Test
    public void verifyvalidV32() {
        verifyV32("valid-ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR089 - RefRequestBody - The $ref of a request body must end with a corresponding suffix", RuleType.BUG, Severity.MINOR, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("default-suffix", "Body", RuleParamType.STRING);
    }
}