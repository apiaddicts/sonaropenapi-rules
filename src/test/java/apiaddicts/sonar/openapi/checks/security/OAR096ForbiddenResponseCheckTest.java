package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR096ForbiddenResponseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR096";
        check = new OAR096ForbiddenResponseCheck();
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
    public void verifyInV2WithoutAuthorizationResponses() {
        verifyV2("without-authorization-responses");
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
    public void verifyInV3WithoutAuthorizationResponses() {
        verifyV3("without-authorization-responses");
    }
    @Test
    public void verifyInV31WithoutAuthorizationResponses() {
        verifyV31("without-authorization-responses");
    }
    @Test
    public void verifyInV32WithoutAuthorizationResponses() {
        verifyV32("without-authorization-responses");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("expected-codes", null, RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR096 - ForbiddenResponses - When defining security schemes, forbidden response codes must be defined", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
}