package org.sonar.samples.openapi.checks.core;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR048AtMostOneBodyParameterCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR048";
        check = new OAR048AtMostOneBodyParameterCheck();
        v2Path = getV2Path("core");
        v3Path = getV3Path("core");
    }

    @Test
    public void verifyInV2() {
        verifyV2("many-body-params");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR048 - AtMostOneBodyParameter - APIs must define at most one body parameter.", RuleType.BUG, Severity.BLOCKER, tags("core"));
    }
}
