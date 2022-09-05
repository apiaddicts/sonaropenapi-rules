package org.sonar.samples.openapi.checks.core;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR047DocumentedTagCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR047";
        check = new OAR047DocumentedTagCheck();
        v2Path = getV2Path("core");
        v3Path = getV3Path("core");
    }

    @Test
    public void verifyInV2() {
        verifyV2("documented-tag");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR047 - DocumentedTag - Tags SHOULD be documented and start with capital letter.", RuleType.BUG, Severity.BLOCKER, tags("core"));
    }
}
