package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR099ApiPrefixBasePathCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR099";
        check = new OAR099ApiPrefixBasePathCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid");
    }

    @Test
    public void verifyInV2WithoutApiPrefix() {
        verifyV2("without-api-prefix");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyInV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyInV32() {
        verifyV32("valid");
    }

    @Test
    public void verifyInV3WithoutApiPrefix() {
        verifyV3("without-api-prefix");
    }
    @Test
    public void verifyInV31WithoutApiPrefix() {
        verifyV31("without-api-prefix");
    }
    @Test
    public void verifyInV32WithoutApiPrefix() {
        verifyV32("without-api-prefix");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR099 - ApiPrefixBasePath - API name must start with the prefix 'api-'", RuleType.BUG, Severity.CRITICAL, tags("format"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(0);
    }
}