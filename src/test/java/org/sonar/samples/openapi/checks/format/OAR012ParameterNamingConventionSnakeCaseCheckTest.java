package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR012ParameterNamingConventionSnakeCaseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR012";
        check = new OAR012ParameterNamingConventionSnakeCaseCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("snake-case-plain");
    }

    @Test
    public void verifyInV2With$ref() {
        verifyV2("snake-case-with-$ref");
    }

    @Test
    public void verifyInV3() {
        verifyV3("snake-case-plain");
    }

    @Test
    public void verifyInV3With$ref() {
        verifyV3("snake-case-with-$ref");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("naming-convention", "snake_case", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR012 - ParameterNamingConventionSnakeCase - Path params names, query params names, object names and property names with more than two words must be compliant with the standard naming convention (snake_case)", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
