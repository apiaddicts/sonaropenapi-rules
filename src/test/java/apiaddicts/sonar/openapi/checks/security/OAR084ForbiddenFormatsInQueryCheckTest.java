package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR084ForbiddenFormatsInQueryCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR084";
        check = new OAR084ForbiddenFormatsInQueryCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid-query-formats");
    }

    @Test
    public void verifyInV2ForbiddenQueryFormats() {
        verifyV2("forbidden-query-formats");
    }

    @Test
    public void verifyInV2NoParameters() {
        verifyV2("no-parameters");
    }

    @Test
    public void verifyInV2NonQueryParam() {
        verifyV2("non-query-param");
    }

    @Test
    public void verifyInV2NullFormatParam() {
        verifyV2("null-format-param");
    }

    @Test
    public void verifyPathFilteringStrategiesInV2() {
        OAR084ForbiddenFormatsInQueryCheck c = (OAR084ForbiddenFormatsInQueryCheck) check;

        c.pathsStr = "/examples";
        c.pathCheckStrategy = "/exclude";
        verifyV2("valid-query-formats");

        c.pathCheckStrategy = "/include";
        verifyV2("valid-query-formats");

        c.pathsStr = "/other";
        verifyV2("valid-query-formats");
    }

    @Test
    public void verifyInV2EmptyForbiddenQueryFormats() {
        ((OAR084ForbiddenFormatsInQueryCheck) check).forbiddenQueryFormatsStr = "";
        verifyV2("valid-query-formats");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid-query-formats");
    }
    @Test
    public void verifyInV31() {
        verifyV31("valid-query-formats");
    }
    @Test
    public void verifyInV32() {
        verifyV32("valid-query-formats");
    }

    @Test
    public void verifyInV3ForbiddenQueryFormats() {
        verifyV3("forbidden-query-formats");
    }
    @Test
    public void verifyInV31ForbiddenQueryFormats() {
        verifyV31("forbidden-query-formats");
    }
    @Test
    public void verifyInV32ForbiddenQueryFormats() {
        verifyV32("forbidden-query-formats");
    }

    @Test
    public void verifyInV3NoParameters() {
        verifyV3("no-parameters");
    }
    @Test
    public void verifyInV31NoParameters() {
        verifyV31("no-parameters");
    }
    @Test
    public void verifyInV32NoParameters() {
        verifyV32("no-parameters");
    }

    @Test
    public void verifyInV3NonQueryParam() {
        verifyV3("non-query-param");
    }
    @Test
    public void verifyInV31NonQueryParam() {
        verifyV31("non-query-param");
    }
    @Test
    public void verifyInV32NonQueryParam() {
        verifyV32("non-query-param");
    }

    @Test
    public void verifyInV3NullFormatParam() {
        verifyV3("null-format-param");
    }
    @Test
    public void verifyInV31NullFormatParam() {
        verifyV31("null-format-param");
    }
    @Test
    public void verifyInV32NullFormatParam() {
        verifyV32("null-format-param");
    }

    @Test
    public void verifyPathFilteringStrategiesInV3() {
        OAR084ForbiddenFormatsInQueryCheck c = (OAR084ForbiddenFormatsInQueryCheck) check;

        c.pathsStr = "/examples";
        c.pathCheckStrategy = "/exclude";
        verifyV3("valid-query-formats");

        c.pathCheckStrategy = "/include";
        verifyV3("valid-query-formats");

        c.pathsStr = "/other";
        verifyV3("valid-query-formats");
    }
    @Test
    public void verifyPathFilteringStrategiesInV31() {
        verifyV31("valid-query-formats");
    }
    @Test
    public void verifyPathFilteringStrategiesInV32() {
        verifyV32("valid-query-formats");
    }

    @Test
    public void verifyInV3EmptyForbiddenQueryFormats() {
        ((OAR084ForbiddenFormatsInQueryCheck) check).forbiddenQueryFormatsStr = "";
        verifyV3("valid-query-formats");
    }
    @Test
    public void verifyInV31EmptyForbiddenQueryFormats() {
        verifyV31("valid-query-formats");
    }
    @Test
    public void verifyInV32EmptyForbiddenQueryFormats() {
        verifyV32("valid-query-formats");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR084 - ForbiddenQueryFormats - Some formats should not pass through this querystring", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("forbidden-query-formats", "password", RuleParamType.STRING);
        assertParameterProperties("paths", "/examples", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/include", RuleParamType.STRING);
    }
}