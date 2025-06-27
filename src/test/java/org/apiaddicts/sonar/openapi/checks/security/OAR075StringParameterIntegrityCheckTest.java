package org.apiaddicts.sonar.openapi.checks.security;

import apiaddicts.sonar.openapi.checks.security.OAR075StringParameterIntegrityCheck;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR075StringParameterIntegrityCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR075";
        check = new OAR075StringParameterIntegrityCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
    }

    @Test
    public void verifyInV2withRestrictions() {
        verifyV2("with-restrictions");
    }

    @Test
    public void verifyInV2noRestrictions() {
        verifyV2("no-restrictions");
    }

    @Test
    public void verifyInV3withRestrictions() {
        verifyV3("with-restrictions");
    }

    @Test
    public void verifyInV3noRestrictions() {
        verifyV3("no-restrictions");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("parameter_integrity", "minLength,maxLength,enum,format", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR075 - StringParameterIntegrityCheck - String parameters should have minLength, maxLength, pattern (regular expression),format or enum restriction", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
}