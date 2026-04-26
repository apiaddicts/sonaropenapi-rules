package apiaddicts.sonar.openapi.checks.owasp;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR073RateLimitCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR073";
        check = new OAR073RateLimitCheck();
        v2Path = getV2Path("owasp");
        v3Path = getV3Path("owasp");
        v31Path = getV31Path("owasp");
        v32Path = getV32Path("owasp");
    }

    @Test
    public void verifyInV2rateLimit() {
        verifyV2("rate-limit");
    }

    @Test
    public void verifyInV2NorateLimit() {
        verifyV2("no-rate-limit");
    }

    @Test
    public void verifyInV3rateLimit() {
        verifyV3("rate-limit");
    }
    @Test
    public void verifyInV31rateLimit() {
        verifyV31("rate-limit");
    }
    @Test
    public void verifyInV32rateLimit() {
        verifyV32("rate-limit");
    }

    @Test
    public void verifyInV3NorateLimit() {
        verifyV3("no-rate-limit");
    }
    @Test
    public void verifyInV31NorateLimit() {
        verifyV31("no-rate-limit");
    }
    @Test
    public void verifyInV32NorateLimit() {
        verifyV32("no-rate-limit");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR073 - RateLimit - API should include a 429 response to indicate rate limiting", RuleType.VULNERABILITY, Severity.MAJOR, tags("owasp"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("paths", null, RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/exclude", RuleParamType.STRING);
    }
}