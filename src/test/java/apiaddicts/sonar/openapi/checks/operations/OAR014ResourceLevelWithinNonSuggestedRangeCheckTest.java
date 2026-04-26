package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR014ResourceLevelWithinNonSuggestedRangeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR014";
        check = new OAR014ResourceLevelWithinNonSuggestedRangeCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
        v31Path = getV31Path("operations");
        v32Path = getV32Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
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

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR014 - ResourceLevelWithinNonSuggestedRangeCheck - Resources depth level should be below the non-suggested range", RuleType.BUG, Severity.MINOR, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("min-level", "4", RuleParamType.INTEGER);
        assertParameterProperties("max-level", "5", RuleParamType.INTEGER);
    }
}
