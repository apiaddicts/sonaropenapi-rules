package org.sonar.samples.openapi.checks.core;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR044MediaTypeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR044";
        check = new OAR044MediaTypeCheck();
        v2Path = getV2Path("core");
        v3Path = getV3Path("core");
    }

    @Test
    public void verifyInV2() {
        verifyV2("media-type");
    }

    @Test
    public void verifyInV3() {
        verifyV3("media-type");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR044 - MediaType - Media types SHOULD conform to the RFC.", RuleType.BUG, Severity.BLOCKER, tags("core"));
    }
}
