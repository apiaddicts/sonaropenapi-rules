package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR012ParameterNamingConventionCheckTest extends BaseCheckTest{
    @Before
    public void init() {
        ruleName = "OAR012";
        check = new OAR012ParameterNamingConventionCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyCaseInV2() {
        verifyV2("snake-case-plain"); //change this name with the name file in resources/checks/v2 and modify de property in the rule class to pass the test
    }

    @Test
    public void verifyCaseWithRefInV2() {
        verifyV2("snake-case-with-$ref"); //change this name with the name file in resources/checks/v2 and modify de property in the rule class to pass the test
    }

    @Test
    public void verifyCaseInV3() {
        verifyV3("snake-case-plain"); //change this name with the name file in resources/checks/v3 and modify de property in the rule class to pass the test with the naming convention
    }
    public void verifyCaseWithRefInV3() {
        verifyV3("snake-case-with-$ref"); //change this name with the name file in resources/checks/v3 and modify de property in the rule class to pass the test with the naming convention
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("naming-convention", "snake_case", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR012 - ParameterNamingConvention - Path params names, query params names, object names and property names with more than two words must be compliant with the standard naming convention", RuleType.BUG, Severity.MINOR, tags("format"));
    }

    @Test
    public void verifyCaseInV31() {
        verifyV31("snake-case-plain");
    }

    @Test
    public void verifyCaseInV32() {
        verifyV32("snake-case-plain");
    }
}
