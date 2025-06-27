package org.apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.format.OAR052UndefinedNumericFormatCheck;

public class OAR052UndefinedNumericFormatCheckTest extends BaseCheckTest {
  @Before
    public void init() {
        ruleName = "OAR052";
        check = new OAR052UndefinedNumericFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyV2() {
        verifyV2("plain");
    }
    @Test
    public void verifyNestedV2() {
        verifyV2("nested");
    }
    @Test
    public void verifyWithRefV2() {
        verifyV2("with-$ref");
    }
    
    @Test
    public void verifyV3() {
        verifyV3("plain");
    }
    @Test
    public void verifyNestedV3() {
        verifyV3("nested");
    }
    @Test
    public void verifyWithRefV3() {
        verifyV3("with-$ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR052 - UndefinedNumericFormat - Numeric types requires a format", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
