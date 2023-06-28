package org.sonar.samples.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR082BinaryOrByteCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR082";
        check = new OAR082BinaryOrByteFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-format");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-format");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR082 - BinaryOrByte - The string properties of the specified parameters must define a byte or binary format.", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("fields-to-apply", "product,line,price", RuleParamType.STRING);
    }
}