package org.sonar.samples.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR073RateLimitCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR073";
        check = new OAR073RateLimitCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
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
    public void verifyInV3NorateLimit() {
        verifyV3("no-rate-limit");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR073 - RateLimit - API should include a 429 response to indicate rate limiting", RuleType.VULNERABILITY, Severity.MAJOR, tags("vulnerability"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("paths", "/status, /health-check", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/exclude", RuleParamType.STRING);
    }
}