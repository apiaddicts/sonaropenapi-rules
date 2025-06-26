package org.sonar.samples.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.format.OAR115VerifyRequiredFields;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR115VerifyRequiredFieldsTest extends BaseCheckTest {
  @Before
    public void init() {
        ruleName = "OAR115";
        check = new OAR115VerifyRequiredFields();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyValidRequiredFieldV2() {
        verifyV2("valid");
    }
    @Test
    public void verifyInvalidRequiredFieldV2() {
        verifyV2("invalid");
    }

    @Test
    public void verifyValidRequiredFieldV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyInvalidRequiredFieldV3() {
        verifyV3("invalid");
    }


    @Override
    public void verifyRule() {
        assertRuleProperties("OAR115 - VerifyRequiredFields - the data in the required field must exist in schema parameters", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
