package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR049NoContentIn204CheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR049";
        check = new OAR049NoContentIn204Check();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
    }

    @Test
    public void verifyInV2() {
        verifyV2("no-content-in-204");
    }

    @Test
    public void verifyInV3() {
        verifyV3("no-content-in-204");
    }
    @Test
    public void verifyInV31() {
        verifyV31("no-content-in-204");
    }
    @Test
    public void verifyInV32() {
        verifyV32("no-content-in-204");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR049 - NoContent204 - 204 responses MUST NOT return content.", RuleType.BUG, Severity.BLOCKER, tags("safety"));
    }
}
