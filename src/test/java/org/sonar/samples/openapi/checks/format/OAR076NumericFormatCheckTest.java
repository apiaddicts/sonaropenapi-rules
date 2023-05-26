package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR076NumericFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR076";
        check = new OAR076NumericFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
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

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Test
    public void verifyInV3WithNested() {
        verifyV3("nested");
    }

    @Test
    public void verifyInV3With$ref() {
        verifyV3("with-$ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR076 - NumericFormat - Numeric types requires a valid format", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}