package org.apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR008AllowedHttpVerbCheck;

public class OAR008AllowedHttpVerbCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR008";
        check = new OAR008AllowedHttpVerbCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
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
        assertRuleProperties("OAR008 - AllowedHttpVerb - Http verb not encouraged", RuleType.BUG, Severity.BLOCKER, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("allowed-verbs", "get,post,put,delete,patch", RuleParamType.STRING);
    }
}
