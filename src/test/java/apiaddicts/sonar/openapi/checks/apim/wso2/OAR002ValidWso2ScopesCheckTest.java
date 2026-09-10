package apiaddicts.sonar.openapi.checks.apim.wso2;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR002ValidWso2ScopesCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR002";
        check = new OAR002ValidWso2ScopesCheck();
        v2Path = getV2Path("apim/wso2");
        v3Path = getV3Path("apim/wso2");
        v31Path = getV31Path("apim");
        v32Path = getV32Path("apim");
    }

    @Test
    public void verifyInV2WithScopes() {
        verifyV2("with-scopes");
    }

    @Test
    public void verifyInV2WithNullScopes() {
        verifyV2("with-null-scopes");
    }

    @Test
    public void verifyInV2WithEmptyScopes() {
        verifyV2("with-empty-scopes");
    }

    @Test
    public void verifyInV2WithoutScopes() {
        verifyV2("without-scopes");
    }

    @Test
    public void verifyInV2WithoutSecurity() {
        verifyV2("without-security");
    }

    @Test
    public void verifyInV3WithScopes() {
        verifyV3("with-scopes");
    }
    @Test
    public void verifyInV31WithScopes() {
        verifyV31("with-scopes");
    }
    @Test
    public void verifyInV32WithScopes() {
        verifyV32("with-scopes");
    }

    @Test
    public void verifyInV3WithNullScopes() {
        verifyV3("with-null-scopes");
    }
    @Test
    public void verifyInV31WithNullScopes() {
        verifyV31("with-null-scopes");
    }
    @Test
    public void verifyInV32WithNullScopes() {
        verifyV32("with-null-scopes");
    }

    @Test
    public void verifyInV3WithEmptyScopes() {
        verifyV3("with-empty-scopes");
    }
    @Test
    public void verifyInV31WithEmptyScopes() {
        verifyV31("with-empty-scopes");
    }
    @Test
    public void verifyInV32WithEmptyScopes() {
        verifyV32("with-empty-scopes");
    }

    @Test
    public void verifyInV3WithoutScopes() {
        verifyV3("without-scopes");
    }
    @Test
    public void verifyInV31WithoutScopes() {
        verifyV31("without-scopes");
    }
    @Test
    public void verifyInV32WithoutScopes() {
        verifyV32("without-scopes");
    }

    @Test
    public void verifyInV3WithoutSecurity() {
        verifyV3("without-security");
    }
    @Test
    public void verifyInV31WithoutSecurity() {
        verifyV31("without-security");
    }
    @Test
    public void verifyInV32WithoutSecurity() {
        verifyV32("without-security");
    }

    @Test
    public void verifyInV2WithEmptyValueScopes() {
        verifyV2("with-empty-value-scopes.yaml");
    }
    @Test
    public void verifyInV3WithEmptyValueScopes() {
        verifyV3("with-empty-value-scopes.yaml");
    }
    @Test
    public void verifyInV31WithEmptyValueScopes() {
        verifyV31("with-empty-value-scopes.yaml");
    }
    @Test
    public void verifyInV32WithEmptyValueScopes() {
        verifyV32("with-empty-value-scopes.yaml");
    }

    @Test
    public void verifyInV2WithEmptyValueContainer() {
        verifyV2("with-empty-value-container.yaml");
    }
    @Test
    public void verifyInV3WithEmptyValueContainer() {
        verifyV3("with-empty-value-container.yaml");
    }
    @Test
    public void verifyInV31WithEmptyValueContainer() {
        verifyV31("with-empty-value-container.yaml");
    }
    @Test
    public void verifyInV32WithEmptyValueContainer() {
        verifyV32("with-empty-value-container.yaml");
    }

    @Test
    public void verifyInV2WithScopesAsMap() {
        verifyV2("with-scopes-as-map.yaml");
    }
    @Test
    public void verifyInV3WithScopesAsMap() {
        verifyV3("with-scopes-as-map.yaml");
    }
    @Test
    public void verifyInV31WithScopesAsMap() {
        verifyV31("with-scopes-as-map.yaml");
    }
    @Test
    public void verifyInV32WithScopesAsMap() {
        verifyV32("with-scopes-as-map.yaml");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR002 - ValidWso2Scopes - WSO2 scope definition is wrong", RuleType.VULNERABILITY, Severity.BLOCKER, tags("api-manager", "vulnerability", "wso2"));
    }
}
