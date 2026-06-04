package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR113CustomFieldCheckTest extends BaseCheckTest {
    @Before
    public void init() {
        ruleName = "OAR113";
        check = new OAR113CustomFieldCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }


    @Test
    public void verifyValidCustomV2() {
        verifyV2("valid");
    }
    @Test
    public void verifyInvalidCustomV2() {
        verifyV2("invalid");
    }

    @Test
    public void verifyValidCustomV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyValidCustomV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyValidCustomV32() {
        verifyV32("valid");
    }
    @Test
    public void verifyInvalidCustomV3() {
        verifyV3("invalid");
    }
    @Test
    public void verifyInvalidCustomV31() {
        verifyV31("invalid");
    }
    @Test
    public void verifyInvalidCustomV32() {
        verifyV32("invalid");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR113 - CustomField - Field or extension must be at the assigned location", RuleType.BUG, Severity.MINOR, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("fieldName", "x-custom-example", RuleParamType.STRING);
        assertParameterProperties("fieldLocation", "path,operation_get,response_200", RuleParamType.STRING);
    }
}
