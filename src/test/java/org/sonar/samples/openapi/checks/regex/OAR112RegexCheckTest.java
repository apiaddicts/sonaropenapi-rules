package org.sonar.samples.openapi.checks.regex;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.regex.OAR112RegexCheck;


public class OAR112RegexCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR112";
        check = new OAR112RegexCheck();
        v2Path = getV2Path("regex");
        v3Path = getV3Path("regex");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }


    @Override
    public void verifyRule() {
        assertRuleProperties("OAR112 - RegexRule - Regex rule", RuleType.BUG, Severity.MINOR, tags("regex"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(6);
        assertParameterProperties("node", "sample", RuleParamType.STRING);
        assertParameterProperties("errorMessage", "sample", RuleParamType.STRING);
        assertParameterProperties("descriptionMessage", "sample", RuleParamType.STRING);
        assertParameterProperties("severityDefault", "sample", RuleParamType.STRING);
        assertParameterProperties("functionDefault", "sample", RuleParamType.STRING);
        assertParameterProperties("expreg", "sample", RuleParamType.STRING);
    }

}