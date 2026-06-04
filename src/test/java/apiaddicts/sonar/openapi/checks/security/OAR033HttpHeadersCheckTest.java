package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR033HttpHeadersCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR033";
        check = new OAR033HttpHeadersCheck();
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
    public void verifyInV2WithForbiddenParams() {
        verifyV2("with-forbidden-params");
    }

    @Test
    public void verifyInV2WithoutRequiredParams() {
        verifyV2("without-required-params");
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
    public void verifyInV3WithForbiddenParams() {
        verifyV3("with-forbidden-params");
    }
    @Test
    public void verifyInV31WithForbiddenParams() {
        verifyV31("with-forbidden-params");
    }
    @Test
    public void verifyInV32WithForbiddenParams() {
        verifyV32("with-forbidden-params");
    }

    @Test
    public void verifyInV3WithoutRequiredParams() {
        verifyV3("without-required-params");
    }
    @Test
    public void verifyInV31WithoutRequiredParams() {
        verifyV31("without-required-params");
    }
    @Test
    public void verifyInV32WithoutRequiredParams() {
        verifyV32("without-required-params");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR033 - HttpHeaders - There are mandatory request headers and others that are not allowed", RuleType.VULNERABILITY, Severity.CRITICAL, tags("safety"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(4);
        assertParameterProperties("mandatory-headers", "x-api-key", RuleParamType.STRING);
        assertParameterProperties("forbidden-headers", "Accept, Content-Type, Authorization", RuleParamType.STRING);
        assertParameterProperties("allowed-headers", "x-api-key, traceId, dateTime", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }
}
