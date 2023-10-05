package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiquality.sonar.openapi.checks.format.OAR095OpenAPIVersionFormatCheck;

public class OAR095OpenAPIVersionFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR095";
        check = new OAR095OpenAPIVersionFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }
    @Test
    public void verifyInV2() {
        verifyV2("invalid-version");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-version");
    }
    @Test
    public void verifyInV3() {
        verifyV3("invalid-version");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-version");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR095 - OpenaAPIVersionFormat - The API version must follow the X.X.X format", RuleType.BUG, Severity.MINOR, tags("format"));
    }
}
