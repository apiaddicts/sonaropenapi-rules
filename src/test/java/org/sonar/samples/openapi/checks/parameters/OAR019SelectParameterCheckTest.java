package org.sonar.samples.openapi.checks.parameters;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR019SelectParameterCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR019";
        check = new OAR019SelectParameterCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
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
    public void verifyInV2With$ref() {
        verifyV2("with-$ref");
    }

    @Test
    public void verifyInV2Without() {
        verifyV2("plain-without");
    }

    @Test
    public void verifyInV2With$refWithout() {
        verifyV2("with-$ref-without");
    }

    @Test
    public void verifyInV2WithoutParameters() {
        verifyV2("without-parameters");
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
    public void verifyInV3With$ref() {
        verifyV3("with-$ref");
    }

    @Test
    public void verifyInV3Without() {
        verifyV3("plain-without");
    }

    @Test
    public void verifyInV3With$refWithout() {
        verifyV3("with-$ref-without");
    }

    @Test
    public void verifyInV3WithoutParameters() {
        verifyV3("without-parameters");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR019 - SelectParameter - $select must be defined as a parameter in this operation", RuleType.BUG, Severity.MINOR, tags("parameters"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("resources-paths", ";get:^\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$", RuleParamType.STRING);
        assertParameterProperties("resources-exclusions", "get:/status", RuleParamType.STRING);
    }
}
