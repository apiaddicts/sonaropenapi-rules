package org.apiaddicts.sonar.openapi.checks.parameters;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.parameters.OAR019SelectParameterCheck;

public class OAR019SelectParameterCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR019";
        check = new OAR019SelectParameterCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
        v31Path = getV31Path("parameters");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2Excluded() {
        verifyV2("excluded");
    }

    @Test
    public void verifyInV2Without() {
        verifyV2("plain-without");
    }

    @Test
    public void verifyInV2WithRef() {
        verifyV2("with-ref");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Test
    public void verifyInV3Excluded() {
        verifyV3("excluded");
    }

    @Test
    public void verifyInV3Without() {
        verifyV3("plain-without");
    }

    @Test
    public void verifyInV3WithRef() {
        verifyV3("with-ref");
    }

    @Test
    public void verifyInV31() {
        verifyV31("plain");
    }

    @Test
    public void verifyInV31Excluded() {
        verifyV31("excluded");
    }

    @Test
    public void verifyInV31Without() {
        verifyV31("plain-without");
    }

    @Test
    public void verifyInV31WithRef() {
        verifyV31("with-ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR019 - SelectParameter - the chosen parameter must be defined in this operation", RuleType.BUG, Severity.MINOR, tags("parameters"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("parameterName", "$select", RuleParamType.STRING);
        assertParameterProperties("paths", "/examples", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/include", RuleParamType.STRING);
        
    }
}