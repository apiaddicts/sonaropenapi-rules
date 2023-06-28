package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR080SecuritySchemasCheckTest extends BaseCheckTest {
    
    @Before
    public void init() {
        ruleName = "OAR080";
        check = new OAR080SecuritySchemasCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2WithSecurity() {
        verifyV2("with-security");
    }

    @Test
    public void verifyInV2NoSecurity() {
        verifyV2("without-security");
    }

    @Test
    public void verifyInV3WithSecurity() {
        verifyV3("with-security");
    }

    @Test
    public void verifyInV3NoSecurity() {
        verifyV3("without-security");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR080 - SecuritySchemas - The security scheme must be among those allowed by the organization and must be complete.", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("expected-security-scheme", "oauth2, apiKey", RuleParamType.STRING);
    }
}