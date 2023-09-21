package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR086DescriptionFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR086";
        check = new OAR086DescriptionFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("invalid-example");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-example");
    }

    @Test
    public void verifyInV3() {
        verifyV3("invalid-example");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-example");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR086 - DescriptionFormat - All descriptions must begin with a capital letter and end with a period", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}