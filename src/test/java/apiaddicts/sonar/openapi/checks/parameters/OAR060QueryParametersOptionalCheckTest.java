package apiaddicts.sonar.openapi.checks.parameters;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR060QueryParametersOptionalCheckTest extends BaseCheckTest{

    @Before
    public void init() {
        ruleName = "OAR060";
        check = new OAR060QueryParametersOptionalCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
        v31Path = getV31Path("parameters");
        v32Path = getV32Path("parameters");
    }

    @Test
    public void verifyInV2requiredFalse() {
        verifyV2("required-false");
    }
    @Test
    public void verifyInV2requiredTrue() {
        verifyV2("required-true");
    }
    @Test
    public void verifyInV3requiredFalse() {
        verifyV3("required-false");
    }
    @Test
    public void verifyInV31requiredFalse() {
        verifyV31("required-false");
    }
    @Test
    public void verifyInV32requiredFalse() {
        verifyV32("required-false");
    }
    @Test
    public void verifyInV3requiredTrue() {
        verifyV3("required-true");
    }
    @Test
    public void verifyInV31requiredTrue() {
        verifyV31("required-true");
    }
    @Test
    public void verifyInV32requiredTrue() {
        verifyV32("required-true");
    }
    @Test
    public void verifyInV2ExcludedPath() {
        verifyV2("excluded-path");
    }
    @Test
    public void verifyInV3ExcludedPath() {
        verifyV3("excluded-path");
    }
    @Test
    public void verifyInV31ExcludedPath() {
        verifyV31("excluded-path");
    }
    @Test
    public void verifyInV32ExcludedPath() {
        verifyV32("excluded-path");
    }
    @Test
    public void verifyNonQueryParamsIgnored() {
        verifyV3("non-query.yaml");
    }
    @Test
    public void verifyPathLevelQueryParam() {
        verifyV3("path-level.yaml");
    }
    @Test
    public void verifyComponentsQueryParam() {
        verifyV3("components-query.yaml");
    }
    @Test
    public void verifySubpathIsNotExcluded() {
        verifyV3("subpath.yaml");
    }
    @Test
    public void verifyEmptyExclusionsFlagsEveryPath() throws Exception {
        setExclusions("");
        verifyV3("no-exclusions.yaml");
    }
    @Test
    public void verifyNullExclusionsFlagsEveryPath() throws Exception {
        setExclusions(null);
        verifyV3("no-exclusions.yaml");
    }
    @Test
    public void verifyRefParamAndQueryWithoutRequiredIgnored() {
        verifyV3("edge-params.yaml");
    }

    private void setExclusions(String value) throws Exception {
        Field field = OAR060QueryParametersOptionalCheck.class.getDeclaredField("exclusionStr");
        field.setAccessible(true);
        field.set(check, value);
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }
    @Override
    public void verifyRule() {
        assertRuleProperties("OAR060 - QueryParametersOptional - All parameters in query must be defined as optional", RuleType.BUG, Severity.CRITICAL, tags("parameters"));
    }

}
