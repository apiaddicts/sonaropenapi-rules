package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR083ForbiddenQueryParamsCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR083";
        check = new OAR083ForbiddenQueryParamsCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("valid-query-params");
    }

    @Test
    public void verifyInV2ForbiddenQueryParams() {
        verifyV2("forbidden-query-params");
    }

    @Test
    public void verifyInV2NoParameters() {
        verifyV2("no-parameters");
    }

    @Test
    public void verifyInV2NullNameParam() {
        verifyV2("null-name-param");
    }

    @Test
    public void verifyInV2OptionsOperation() {
        verifyV2("options-operation");
    }

    @Test
    public void verifyPathFilteringStrategiesInV2() {
        OAR083ForbiddenQueryParamsCheck c = (OAR083ForbiddenQueryParamsCheck) check;

        c.pathsStr = "/examples";
        c.pathCheckStrategy = "/exclude";
        verifyV2("valid-query-params");

        c.pathCheckStrategy = "/include";
        verifyV2("valid-query-params");

        c.pathsStr = "/other";
        verifyV2("valid-query-params");
    }

    @Test
    public void verifyInV2EmptyForbiddenQueryParams() {
        ((OAR083ForbiddenQueryParamsCheck) check).forbiddenQueryParamsStr = "";
        verifyV2("valid-query-params");
    }

    @Test
    public void verifyInV3() {
        verifyV3("valid-query-params");
    }

    @Test
    public void verifyInV3ForbiddenQueryParams() {
        verifyV3("forbidden-query-params");
    }

    @Test
    public void verifyInV3NoParameters() {
        verifyV3("no-parameters");
    }

    @Test
    public void verifyInV3NullNameParam() {
        verifyV3("null-name-param");
    }

    @Test
    public void verifyInV3OptionsOperation() {
        verifyV3("options-operation");
    }

    @Test
    public void verifyPathFilteringStrategiesInV3() {
        OAR083ForbiddenQueryParamsCheck c = (OAR083ForbiddenQueryParamsCheck) check;

        c.pathsStr = "/examples";
        c.pathCheckStrategy = "/exclude";
        verifyV3("valid-query-params");

        c.pathsStr = "/items";
        c.pathCheckStrategy = "/include";
        verifyV3("valid-query-params");

        c.pathsStr = "/other";
        verifyV3("valid-query-params");
    }

    @Test
    public void verifyInV3EmptyForbiddenQueryParams() {
        ((OAR083ForbiddenQueryParamsCheck) check).forbiddenQueryParamsStr = "";
        verifyV3("valid-query-params");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR083 - ForbiddenQueryParams - Some parameters should not pass through this querystring", RuleType.VULNERABILITY, Severity.MAJOR, tags("safety"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("forbidden-query-params", "email, password", RuleParamType.STRING);
        assertParameterProperties("paths", "/examples", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/include", RuleParamType.STRING);
    }
}