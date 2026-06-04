package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR077ParametersInQuerySnakeCaseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR077";
        check = new OAR077ParametersInQuerySnakeCaseCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("not-valid-in-query");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-in-query");
    }

    @Test
    public void verifyInV3() {
        verifyV3("not-valid-in-query");
    }
    @Test
    public void verifyInV31() {
        verifyV31("not-valid-in-query");
    }
    @Test
    public void verifyInV32() {
        verifyV32("not-valid-in-query");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-in-query");
    }
    @Test
    public void verifyvalidV31() {
        verifyV31("valid-in-query");
    }
    @Test
    public void verifyvalidV32() {
        verifyV32("valid-in-query");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR077 - ParametersInQuerySnakeCase - All parameters in query must be snake_case", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}