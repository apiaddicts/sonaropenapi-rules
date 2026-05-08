package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR044MediaTypeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR044";
        check = new OAR044MediaTypeCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2() {
        verifyV2("media-type");
    }

    @Test
    public void verifyInV3() {
        verifyV3("media-type");
    }
    @Test
    public void verifyInV31() {
        verifyV31("media-type");
    }
    @Test
    public void verifyInV32() {
        verifyV32("media-type");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR044 - MediaType - Media types SHOULD conform to the RFC.", RuleType.BUG, Severity.BLOCKER, tags("format"));
    }
}
