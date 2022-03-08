package org.sonar.samples.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR003DefinedWso2ScopesDescriptionCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR003";
        check = new OAR003DefinedWso2ScopesDescriptionCheck();
        v2Path = getV2Path("security");
        v3Path = getV3Path("security");
    }

    @Test
    public void verifyInV2WithScopeDescription() {
        verifyV2("with-description");
    }

    @Test
    public void verifyInV2WithoutScopeDescription() {
        verifyV2("without-description");
    }

    @Test
    public void verifyInV2WithNullScopeDescription() {
        verifyV2("with-null-description");
    }

    @Test
    public void verifyInV3WithScopeDescription() {
        verifyV3("with-description");
    }

    @Test
    public void verifyInV3WithoutScopeDescription() {
        verifyV3("without-description");
    }

    @Test
    public void verifyInV3WithNullScopeDescription() {
        verifyV3("with-null-description");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR003 - DefinedWso2ScopesDescription - WSO2 scope description is recommended", RuleType.BUG, Severity.BLOCKER, tags("vulnerability"));
    }
}
