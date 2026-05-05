package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR037StringFormatCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR037";
        check = new OAR037StringFormatCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2WithNested() {
        verifyV2("nested");
    }

    @Test
    public void verifyInV2With$ref() {
        verifyV2("with-$ref");
    }

    @Test
    public void verifyInV3() {
        verifyV3("complete");
    }
    @Test
    public void verifyInV31() {
        verifyV31("complete");
    }
    @Test
    public void verifyInV32() {
        verifyV32("complete");
    }

    @Test
    public void verifyInV3WithNested() {
        verifyV3("nested");
    }
    @Test
    public void verifyInV31WithNested() {
        verifyV31("nested");
    }
    @Test
    public void verifyInV32WithNested() {
        verifyV32("nested");
    }

    @Test
    public void verifyInV3With$ref() {
        verifyV3("with-$ref");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("formats-allowed", "date,date-time,password,byte,binary,email,uuid,uri,hostname,ipv4,ipv6,HEX,HEX(16)", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR037 - StringFormat - String types requires a valid format", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
}
