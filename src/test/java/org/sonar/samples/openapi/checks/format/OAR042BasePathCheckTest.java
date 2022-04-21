package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR042BasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR042";
        check = new OAR042BasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2TooShort() {
        verifyV2("too-short");
    }

    @Test
    public void verifyInV2TooLong() {
        verifyV2("too-long");
    }

    @Test
    public void verifyInV2WithoutApiPrefix() {
        verifyV2("without-api-prefix");
    }

    @Test
    public void verifyInV2IncorrectVersion() {
        verifyV2("incorrect-version");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }

    @Test
    public void verifyInV3TooShort() {
        verifyV3("too-short");
    }

    @Test
    public void verifyInV3TooLong() {
        verifyV3("too-long");
    }

    @Test
    public void verifyInV3WithoutApiPrefix() {
        verifyV3("without-api-prefix");
    }

    @Test
    public void verifyInV3IncorrectVersion() {
        verifyV3("incorrect-version");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR042 - BasePath - Base path must be compliant with the standard", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }
}
