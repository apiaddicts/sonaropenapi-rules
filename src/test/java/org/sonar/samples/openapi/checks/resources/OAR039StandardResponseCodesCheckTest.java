package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR039StandardResponseCodesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR039";
        check = new OAR039StandardResponseCodesCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2MissingCodes() {
        verifyV2("missing-codes");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3MissingCodes() {
        verifyV3("missing-codes");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR039 - StandardResponseCodes - Responses must be compliant with the standard", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("required-codes-by-resources-paths", ";get:^\\/[^\\/{}]*$:200|206,400,500,504;get:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$:200,400,404,500,504;post:^\\/[^\\/{}]*$:201|202,400,413,415,500,504;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*$:201|202,400,404,413,415,500,504;post:^\\/[^\\/{}]*\\/get$:200,400,404,413,415,500,504;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*\\/get$:200,400,404,413,415,500,504;post:^\\/[^\\/{}]*\\/delete$:200,400,404,413,415,500,504;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*\\/delete$:200,400,404,413,415,500,504;put:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$:200,400,404,413,415,500,504;delete:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$:200,400,404,500,504;patch:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$:200,400,404,413,415,500,504", RuleParamType.STRING);
        assertParameterProperties("resources-exclusions", "get:/status", RuleParamType.STRING);
    }
}
