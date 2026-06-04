package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR051DescriptionDiffersSummaryCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR051";
        check = new OAR051DescriptionDiffersSummaryCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("different-description");
    }

    @Test
    public void verifyInV3() {
        verifyV3("different-description");
    }
    @Test
    public void verifyInV31() {
        verifyV31("different-description");
    }
    @Test
    public void verifyInV32() {
        verifyV32("different-description");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR051 - DescriptionDiffersSummary - Operation description must differ from its summary.", RuleType.BUG, Severity.BLOCKER, tags("format"));
    }
}
