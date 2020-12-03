package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR052UndefinedNumericFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR052";
        check = new OAR052UndefinedNumericFormatCheck();
        v2Path = getV2Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2WithNested() {
        verifyV2("nested");
    }

    @Test
    public void verifyInV2With$ref() {
        verifyV2("with-$ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR052 - UndefinedNumericFormat - Numeric types requires a format", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
