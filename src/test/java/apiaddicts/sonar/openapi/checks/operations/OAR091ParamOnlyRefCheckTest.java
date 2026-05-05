package apiaddicts.sonar.openapi.checks.operations;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR091ParamOnlyRefCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR091";
        check = new OAR091ParamOnlyRefCheck();
        v2Path = getV2Path("operations");
        v3Path = getV3Path("operations");
        v31Path = getV31Path("operations");
        v32Path = getV32Path("operations");
    }

    @Test
    public void verifyInV2noref() {
        verifyV2("no-ref");
    }

    @Test
    public void verifyInV2withref() {
        verifyV2("with-ref");
    }

    @Test
    public void verifyInV3noref() {
        verifyV3("no-ref");
    }
    @Test
    public void verifyInV31noref() {
        verifyV31("no-ref");
    }
    @Test
    public void verifyInV32noref() {
        verifyV32("no-ref");
    }

    @Test
    public void verifyInV3withref() {
        verifyV3("with-ref");
    }
    @Test
    public void verifyInV31withref() {
        verifyV31("with-ref");
    }
    @Test
    public void verifyInV32withref() {
        verifyV32("with-ref");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR091 - ParamOnlyRef - Parameters must contain only references ($ref)", RuleType.BUG, Severity.MAJOR, tags("operations"));
    }
}