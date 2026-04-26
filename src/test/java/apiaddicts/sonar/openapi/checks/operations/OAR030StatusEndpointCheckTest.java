package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR030StatusEndpointCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR030";
        check = new OAR030StatusEndpointCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
        v31Path = getV31Path("operations");
        v32Path = getV32Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithoutStatus() {
        verifyV2("without-status");
    }

    @Test
    public void verifyInV2WithStatusWithoutGet() {
        verifyV2("with-status-without-get");
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
    public void verifyInV3WithoutStatus() {
        verifyV3("without-status");
    }
    @Test
    public void verifyInV31WithoutStatus() {
        verifyV31("without-status");
    }
    @Test
    public void verifyInV32WithoutStatus() {
        verifyV32("without-status");
    }

    @Test
    public void verifyInV3WithStatusWithoutGet() {
        verifyV3("with-status-without-get");
    }
    @Test
    public void verifyInV31WithStatusWithoutGet() {
        verifyV31("with-status-without-get");
    }
    @Test
    public void verifyInV32WithStatusWithoutGet() {
        verifyV32("with-status-without-get");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR030 - StatusEndpoint - Status endpoint must be declared", RuleType.BUG, Severity.BLOCKER, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("method", "get", RuleParamType.STRING);
        assertParameterProperties("status-endpoint", "/status", RuleParamType.STRING);
    }
}
