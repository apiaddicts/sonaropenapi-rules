package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.format.OAR016NumericFormatCheck;

public class OAR016NumericFormatCheckTest extends BaseCheckTest {
  @Before
    public void init() {
        ruleName = "OAR016";
        check = new OAR016NumericFormatCheck();
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
        assertRuleProperties("OAR016 - NumericFormat - Numeric types requires a valid format", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
  
}
