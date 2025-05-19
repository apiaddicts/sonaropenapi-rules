package org.sonar.samples.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR018ResourcesByVerbCheck;


public class OAR018ResourcesByVerbCheckTest extends BaseCheckTest {

private  String defaultResources = ";get:^\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;get:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;post:^\\/[^\\/{}]*$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*$;post:^\\/[^\\/{}]*\\/get$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/get$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/get$;post:^\\/[^\\/{}]*\\/delete$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/delete$;post:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/delete$;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;put:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;patch:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$;delete:^\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)\\/[^\\/{}]*\\/(\\{[^\\/{}]*\\}|\\bme\\b)$";

    @Before
        public void init() {
            ruleName = "OAR018";
            check = new OAR018ResourcesByVerbCheck();
            v2Path = getV2Path("operations");
            v3Path = getV3Path("operations");
        } 


    @Test
    public void verifyInV2(){
      verifyV2("plain");
    }
     @Test
    public void verifyInV3(){
      verifyV3("plain");
    }


    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("allowed-resources-paths", defaultResources, RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR018 - ResourcesByVerb - Operation not recommended for resource path", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }
}
