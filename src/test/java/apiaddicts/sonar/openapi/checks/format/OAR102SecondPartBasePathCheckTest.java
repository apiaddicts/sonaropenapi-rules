package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR102SecondPartBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR102";
        check = new OAR102SecondPartBasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifySecondPartBasePathLogicInV2() {
        ((OAR102SecondPartBasePathCheck) check).secondPartValuesStr = "v1";

        verifyV2("invalid");
        verifyV2("valid-with-values");
        verifyV2("one-part-path");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifySecondPartBasePathLogicInV3() {
        ((OAR102SecondPartBasePathCheck) check).secondPartValuesStr = "v1";

        verifyV3("invalid");
        verifyV3("valid-with-values");
        verifyV3("one-part-path");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR102 - SecondPartBasePath - The second part of the path should be one of the alloweds", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("second-part-values", "-", RuleParamType.STRING);
    }
}