package org.sonar.samples.openapi.checks.format;

import apiquality.sonar.openapi.checks.format.OAR113CustomFieldCheck;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR113CustomFieldCheckTest extends BaseCheckTest {
    @Before
    public void init() {
        ruleName = "OAR113";
        check = new OAR113CustomFieldCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
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
    public void verifyInvalidCustomV3() {
        verifyV3("invalid");
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
