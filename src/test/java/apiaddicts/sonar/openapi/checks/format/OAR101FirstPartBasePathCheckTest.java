package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR101FirstPartBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR101";
        check = new OAR101FirstPartBasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyFirstPartBasePathLogicInV2() {
        ((OAR101FirstPartBasePathCheck) check).firstPartValuesStr = "api-seguros";

        verifyV2("invalid");
        verifyV2("valid-with-values");
        verifyV2("empty-path");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyFirstPartBasePathLogicInV3() {
        ((OAR101FirstPartBasePathCheck) check).firstPartValuesStr = "api-seguros";

        verifyV3("invalid");
        verifyV3("valid-with-values");
        verifyV3("empty-path");
    }


    @Override
    public void verifyRule() {
        assertRuleProperties("OAR101 - FirstPartBasePath - The first part of the path should be one of the alloweds", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("first-part-values", "-", RuleParamType.STRING);
    }
}