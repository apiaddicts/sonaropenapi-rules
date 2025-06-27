package org.apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR032AmbiguousElementsPathCheck;

public class OAR032AmbiguousElementsPathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR032";
        check = new OAR032AmbiguousElementsPathCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithForbiddenNames() {
        verifyV2("forbidden-names");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3WithForbiddenNames() {
        verifyV3("forbidden-names");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR032 - AmbiguousElementsPath - Ambiguous path parts not encouraged", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("ambiguous-names",
                "elementos,instancias,recursos,valores,terminos,objetos,articulos,elements," +
                        "instances,resources,values,terms,objects,items",
                RuleParamType.STRING);
    }
}
