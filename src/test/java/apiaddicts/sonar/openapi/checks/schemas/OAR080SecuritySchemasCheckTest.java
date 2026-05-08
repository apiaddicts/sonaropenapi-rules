package apiaddicts.sonar.openapi.checks.schemas;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR080SecuritySchemasCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR080";
        check = new OAR080SecuritySchemasCheck();
        v2Path = getV2Path("schemas");
        v3Path = getV3Path("schemas");
        v31Path = getV31Path("schemas");
        v32Path = getV32Path("schemas");
    }

    @Test
    public void verifyV2WithSecurity() {
        verifyV2("with-security");
    }

    @Test
    public void verifyV2WithoutSecurity() {
        verifyV2("without-security");
    }

    @Test
    public void verifyV2GlobalSecurity() {
        verifyV2("global-security");
    }

    @Test
    public void verifyV2WrongScheme() {
        verifyV2("wrong-scheme");
    }

    @Test
    public void verifyV2EmptySecurity() {
        verifyV2("empty-security");
    }

    @Test
    public void verifyV2EmptyGlobalSecurity() {
        verifyV2("empty-global-security");
    }

    @Test
    public void verifyV3WithSecurity() {
        verifyV3("with-security");
    }

    @Test
    public void verifyV3WithoutSecurity() {
        verifyV3("without-security");
    }

    @Test
    public void verifyV3GlobalSecurity() {
        verifyV3("global-security");
    }

    @Test
    public void verifyV3WrongScheme() {
        verifyV3("wrong-scheme");
    }

    @Test
    public void verifyV3EmptySecurity() {
        verifyV3("empty-security");
    }

    @Test
    public void verifyV3EmptyGlobalSecurity() {
        verifyV3("empty-global-security");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties(
            "OAR080 - SecuritySchemas - The security scheme must be among those allowed by the organization and must be complete.",
            RuleType.VULNERABILITY,
            Severity.MAJOR,
            tags("schemas")
        );
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("expected-security-scheme", "oauth2, apiKey", RuleParamType.STRING);
    }
}
