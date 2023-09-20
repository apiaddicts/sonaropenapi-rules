package org.sonar.samples.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.security.OAR070BrokenAccessControlCheck;


public class OAR070BrokenAccessControlCheckTest extends BaseCheckTest{
    
    @Before
    public void init() {
        ruleName = "OAR070";
        check = new OAR070BrokenAccessControlCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
    }

    @Test
    public void verifyInV2numeric() {
        verifyV2("numeric");
    }
    @Test
    public void verifyInV2noNumeric() {
        verifyV2("no-numeric");
    }
    @Test
    public void verifyInV3numeric() {
        verifyV3("numeric");
    }
    @Test
    public void verifyInV3noNumeric() {
        verifyV3("no-numeric");
    }
    @Override
    public void verifyRule() {
        assertRuleProperties("OAR070 - BrokenAccessControl - Parameters in path shouldn't be numeric", RuleType.VULNERABILITY, Severity.MAJOR, tags("vulnerability"));
    }
    
}
