package apiaddicts.sonar.openapi.checks.operations;

import apiaddicts.sonar.openapi.BaseCheckTest;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;

public class OAR017ResourcePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR017";
        check = new OAR017ResourcePathCheck();
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
        assertRuleProperties("OAR017 - ResourcePath - Resource path should alternate static and parametrized parts", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("exclude_patterns", "get,me,search", RuleParamType.STRING);
    }

}