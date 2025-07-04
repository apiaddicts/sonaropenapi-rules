package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

import apiaddicts.sonar.openapi.checks.operations.OAR048AtMostOneBodyParameterCheck;

public class OAR048AtMostOneBodyParameterCheckTest extends BaseCheckTest{


 @Before
        public void init() {
            ruleName = "OAR048";
            check = new OAR048AtMostOneBodyParameterCheck();
            v2Path = getV2Path("operations");
            v3Path = getV3Path("operations");
        } 


    @Test
    public void verifyInV2(){
      verifyV2("many-body-params");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR048 - AtMostOneBodyParameter - APIs must define at most one body parameter.", RuleType.BUG, Severity.BLOCKER, tags("operations"));
    } 
}
