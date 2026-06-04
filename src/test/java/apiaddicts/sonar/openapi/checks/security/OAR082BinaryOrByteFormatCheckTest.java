package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR082BinaryOrByteFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR082";
        check = new OAR082BinaryOrByteFormatCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }
    @Test
    public void verifyvalidV2() {
        verifyV2("valid-format");
    }
    @Test
    public void verifyvalidV3() {
        verifyV3("valid-format");
    }
    @Test
    public void verifyvalidV31() {
        verifyV31("valid-format");
    }
    @Test
    public void verifyvalidV32() {
        verifyV32("valid-format");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR082 - BinaryOrByte - The string properties of the specified parameters must define a byte or binary format.", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }
    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("fields-to-apply", "product,line,price", RuleParamType.STRING);
    }
}
