package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.format.OAR086DescriptionFormatCheck;

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

    @Test
    public void verifyvalidV3internalRef() {
        verifyV3("internal-refexample.yaml");
    }

    @Test
    public void verifyvalidV3externalRef() {
        verifyV3("external-refexample.yaml");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR086 - DescriptionFormat - Descriptions must begin with a capital letter, end with a period and not be empty", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}