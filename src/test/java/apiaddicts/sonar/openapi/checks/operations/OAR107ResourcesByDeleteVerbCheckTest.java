package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR107ResourcesByDeleteVerbCheck;

public class OAR107ResourcesByDeleteVerbCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR107";
        check = new OAR107ResourcesByDeleteVerbCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR107 - ResourcesByDeleteVerb - Delete Operation not recommended for resource path", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("words-to-exclude", "get,delete", RuleParamType.STRING);
    }
}