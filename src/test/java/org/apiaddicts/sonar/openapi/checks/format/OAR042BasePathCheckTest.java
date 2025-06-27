package org.apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.format.OAR042BasePathCheck;

public class OAR042BasePathCheckTest extends BaseCheckTest{
   @Before
    public void init() {
        ruleName = "OAR042";
        check = new OAR042BasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyIncorrectVersionV2() {
        verifyV2("incorrect-version");
    }

    @Test
    public void verifyTooLongV2() {
        verifyV2("too-long");
    }
  
    @Test
    public void verifyTooShortV2() {
        verifyV2("too-short");
    }

    @Test
    public void verifyV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyIncorrectVersionV3() {
        verifyV3("incorrect-version");
    }

    @Test
    public void verifyTooLongV3() {
        verifyV3("too-long");
    }
  
    @Test
    public void verifyTooShortV3() {
        verifyV3("too-short");
    }


    @Override
    public void verifyRule() {
        assertRuleProperties("OAR042 - BasePath - Base path must be compliant with the standard", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }
}
