package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR040StandardWso2ScopesNameCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR040";
        check = new OAR040StandardWso2ScopesNameCheck();
        v2Path = getV2Path("apim/wso2");
        v3Path = getV3Path("apim/wso2");
        v31Path = getV31Path("apim");
        v32Path = getV32Path("apim");
    }

    @Test
    public void verifyInV2WithValidNames() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithInvalidNames() {
        verifyV2("invalid");
    }

    @Test
    public void verifyInV3WithValidNames() {
        verifyV3("valid");
    }
    @Test
    public void verifyInV31WithValidNames() {
        verifyV31("valid");
    }
    @Test
    public void verifyInV32WithValidNames() {
        verifyV32("valid");
    }

    @Test
    public void verifyInV3WithInvalidNames() {
        verifyV3("invalid");
    }
    @Test
    public void verifyInV31WithInvalidNames() {
        verifyV31("invalid");
    }
    @Test
    public void verifyInV32WithInvalidNames() {
        verifyV32("invalid");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR040 - StandardWso2ScopesName - WSO2 scope name is non compliant with the standard", RuleType.VULNERABILITY, Severity.BLOCKER, tags("api-manager", "vulnerability", "wso2"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("pattern", "^[a-zA-Z]{4,}_(SC|sc)_[a-zA-Z0-9]{1,}$", RuleParamType.STRING);
    }
}
