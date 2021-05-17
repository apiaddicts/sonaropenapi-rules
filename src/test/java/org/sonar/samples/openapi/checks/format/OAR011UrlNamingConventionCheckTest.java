package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR011UrlNamingConventionCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR011";
        check = new OAR011UrlNamingConventionCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2BasePathOk() {
        verifyV2("base-path-ok");
    }

    @Test
    public void verifyInV2BasePathWrong() {
        verifyV2("base-path-wrong");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Test
    public void verifyInV3BasePathOk() {
        verifyV3("base-path-ok");
    }

    @Test
    public void verifyInV3BasePathWrong() {
        verifyV3("base-path-wrong");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("default-naming-convention", "kebab-case", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR011 - UrlNamingConvention - Non variable path parts must be in kebab-case", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
}
