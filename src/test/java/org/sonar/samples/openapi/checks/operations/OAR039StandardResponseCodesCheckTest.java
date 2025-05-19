package org.sonar.samples.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR039StandardResponseCodesCheck;

public class OAR039StandardResponseCodesCheckTest extends BaseCheckTest{

  private String defaultCodesByRosources = ";get:^\\/[^\\/{}]*$:200|206,400,500,503;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$:200|206,400,500,503,404;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$:200|206,400,500,503,404;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;post:^\\/[^\\/{}]*$:200|201|202,400,415,500,503;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$:200|201|202,400,415,500,503,404;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$:200|201|202,400,415,500,503,404;post:^\\/[^\\/{}]*\\/get$:200,404,400,415,500,503;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/get$:200,404,400,415,500,503;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/get$:200,404,400,415,500,503;post:^\\/[^\\/{}]*\\/delete$:200,404,400,415,500,503;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/delete$:200,404,400,415,500,503;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/delete$:200,404,400,415,500,503;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,400,404,500,503;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$:200,404,400,415,500,503";



 @Before
        public void init() {
            ruleName = "OAR039";
            check = new OAR039StandardResponseCodesCheck();
            v2Path = getV2Path("operations");
            v3Path = getV3Path("operations");
        } 


    @Test
    public void verifyInV2(){
      verifyV2("valid");
    }
    @Test
    public void verifyMissingCodesV2(){
      verifyV2("missing-codes");
    }

     @Test
    public void verifyInV3(){
      verifyV3("valid");
    }
    @Test
    public void verifyMissingCodesV3(){
      verifyV3("missing-codes");
    }


    @Override
    public void verifyParameters() {
        assertNumberOfParameters(2);
        assertParameterProperties("required-codes-by-resources-paths", defaultCodesByRosources, RuleParamType.STRING);
        assertParameterProperties("resources-exclusions", "get:/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR039 - StandardResponseCodes - Response codes must be defined according to the standard", RuleType.BUG, Severity.MAJOR, tags("operations"));
    } 
}
