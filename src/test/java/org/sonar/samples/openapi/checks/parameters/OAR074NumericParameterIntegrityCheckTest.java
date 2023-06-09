package org.sonar.samples.openapi.checks.parameters;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR074NumericParameterIntegrityCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR074";
        check = new OAR074NumericParameterIntegrityCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
    }

    @Test
    public void verifyInV2withRestrictions() {
        verifyV2("with-restrictions");
    }

    @Test
    public void verifyInV2noRestrictions() {
        verifyV2("no-restrictions");
    }

    @Test
    public void verifyInV3withRestrictions() {
        verifyV3("with-restrictions");
    }

    @Test
    public void verifyInV3noRestrictions() {
        verifyV3("no-restrictions");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR074 - NumericParameterIntegrityCheck - Numeric parameters should have minimum, maximum, or format restriction", RuleType.BUG, Severity.MAJOR, tags("parameters"));
    }
}