package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR046DeclaredTagCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR046";
        check = new OAR046DeclaredTagCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
        v31Path = getV31Path("operations");
        v32Path = getV32Path("operations");
    }

    @Test
    public void verifyInV2() {
        verifyV2("declared-tag");
    }

    @Test
    public void verifyInV3() {
        verifyV3("declared-tag");
    }
    @Test
    public void verifyInV31() {
        verifyV31("declared-tag");
    }
    @Test
    public void verifyInV32() {
        verifyV32("declared-tag");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR046 - DeclaredTag - Each operation SHOULD have a tag.", RuleType.BUG, Severity.BLOCKER, tags("operations"));
    }
}
