package apiaddicts.sonar.openapi.checks.apim.wso2;

import apiaddicts.sonar.openapi.BaseCheckTest;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;

public class OAR005UndefinedWso2ScopeUseCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR005";
        check = new OAR005UndefinedWso2ScopeUseCheck();
        v2Path = getV2Path("apim/wso2");
        v3Path = getV3Path("apim/wso2");
        v31Path = getV31Path("apim");
        v32Path = getV32Path("apim");
    }

    @Test
    public void verifyInV2WithNullOperationScope() {
        verifyV2("with-null-operation-scope");
    }

    @Test
    public void verifyInV2WithWrongOperationScope() {
        verifyV2("with-wrong-operation-scope");
    }

    @Test
    public void verifyInV2WithCorrectOperationScope() {
        verifyV2("with-correct-operation-scope");
    }

    @Test
    public void verifyInV3WithNullOperationScope() {
        verifyV3("with-null-operation-scope");
    }
    @Test
    public void verifyInV31WithNullOperationScope() {
        verifyV31("with-null-operation-scope");
    }
    @Test
    public void verifyInV32WithNullOperationScope() {
        verifyV32("with-null-operation-scope");
    }

    @Test
    public void verifyInV3WithWrongOperationScope() {
        verifyV3("with-wrong-operation-scope");
    }
    @Test
    public void verifyInV31WithWrongOperationScope() {
        verifyV31("with-wrong-operation-scope");
    }
    @Test
    public void verifyInV32WithWrongOperationScope() {
        verifyV32("with-wrong-operation-scope");
    }

    @Test
    public void verifyInV3WithCorrectOperationScope() {
        verifyV3("with-correct-operation-scope");
    }
    @Test
    public void verifyInV31WithCorrectOperationScope() {
        verifyV31("with-correct-operation-scope");
    }
    @Test
    public void verifyInV32WithCorrectOperationScope() {
        verifyV32("with-correct-operation-scope");
    }

    @Test
    public void verifyInV2WithRefSecurity() {
        verifyV2("with-ref-security");
    }
    @Test
    public void verifyInV3WithRefSecurity() {
        verifyV3("with-ref-security");
    }
    @Test
    public void verifyInV31WithRefSecurity() {
        verifyV31("with-ref-security");
    }
    @Test
    public void verifyInV32WithRefSecurity() {
        verifyV32("with-ref-security");
    }

    @Test
    public void verifyInV2WithChainedRefSecurity() {
        verifyV2("with-chained-ref-security");
    }
    @Test
    public void verifyInV3WithChainedRefSecurity() {
        verifyV3("with-chained-ref-security");
    }
    @Test
    public void verifyInV31WithChainedRefSecurity() {
        verifyV31("with-chained-ref-security");
    }
    @Test
    public void verifyInV32WithChainedRefSecurity() {
        verifyV32("with-chained-ref-security");
    }

    @Test
    public void verifyInV2WithScopesAsMap() {
        verifyV2("with-scopes-as-map");
    }
    @Test
    public void verifyInV3WithScopesAsMap() {
        verifyV3("with-scopes-as-map");
    }
    @Test
    public void verifyInV31WithScopesAsMap() {
        verifyV31("with-scopes-as-map");
    }
    @Test
    public void verifyInV32WithScopesAsMap() {
        verifyV32("with-scopes-as-map");
    }

    @Test
    public void verifyInV2WithRefScope() {
        verifyV2("with-ref-scope");
    }
    @Test
    public void verifyInV3WithRefScope() {
        verifyV3("with-ref-scope");
    }
    @Test
    public void verifyInV31WithRefScope() {
        verifyV31("with-ref-scope");
    }
    @Test
    public void verifyInV32WithRefScope() {
        verifyV32("with-ref-scope");
    }

    @Test
    public void verifyInV2WithScopeKeyNotName() {
        verifyV2("with-scope-key-not-name");
    }
    @Test
    public void verifyInV3WithScopeKeyNotName() {
        verifyV3("with-scope-key-not-name");
    }
    @Test
    public void verifyInV31WithScopeKeyNotName() {
        verifyV31("with-scope-key-not-name");
    }
    @Test
    public void verifyInV32WithScopeKeyNotName() {
        verifyV32("with-scope-key-not-name");
    }

    @Test
    public void verifyInV2WithoutSecurity() {
        verifyV2("without-security");
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
    public void verifyInV3WithNullSecurity() {
        verifyV3("with-null-security.yaml");
    }

    @Test
    public void verifyInV3WithScalarSecurity() {
        verifyV3("with-scalar-security.yaml");
    }

    @Test
    public void verifyInV3WithArraySecurity() {
        verifyV3("with-array-security.yaml");
    }

    @Test
    public void verifyInV3WithoutApim() {
        verifyV3("without-apim.yaml");
    }

    @Test
    public void verifyInV3WithNullApim() {
        verifyV3("with-null-apim.yaml");
    }

    @Test
    public void verifyInV3WithoutScopes() {
        verifyV3("without-scopes.yaml");
    }

    @Test
    public void verifyInV3WithNullScopes() {
        verifyV3("with-null-scopes.yaml");
    }

    @Test
    public void verifyInV3WithEmptyArrayScopes() {
        verifyV3("with-empty-array-scopes.yaml");
    }

    @Test
    public void verifyInV3WithEmptyObjectScopes() {
        verifyV3("with-empty-object-scopes.yaml");
    }

    @Test
    public void verifyInV3WithScalarScopes() {
        verifyV3("with-scalar-scopes.yaml");
    }

    @Test
    public void verifyInV3WithCyclicRefSecurity() {
        verifyV3("with-cyclic-ref-security.yaml");
    }

    @Test
    public void verifyInV3WithDanglingRefSecurity() {
        verifyV3("with-dangling-ref-security.yaml");
    }

    @Test
    public void verifyInV3WithMalformedScopes() {
        verifyV3("with-malformed-scopes.yaml");
    }

    @Test
    public void verifyInV3WithXScopeNullSpellings() {
        verifyV3("with-x-scope-null-spellings.yaml");
    }

    @Test
    public void verifyInV3WithXScopeScalarTypes() {
        verifyV3("with-x-scope-scalar-types.yaml");
    }

    @Test
    public void verifyInV3WithXScopeCollections() {
        verifyV3("with-x-scope-collections.yaml");
    }

    @Test
    public void verifyInV3WithXScopeBlockCollections() {
        verifyV3("with-x-scope-block-collections.yaml");
    }

    @Test
    public void verifyInV2WithAllVerbs() {
        verifyV2("with-all-verbs");
    }
    @Test
    public void verifyInV3WithAllVerbs() {
        verifyV3("with-all-verbs");
    }
    @Test
    public void verifyInV31WithAllVerbs() {
        verifyV31("with-all-verbs");
    }
    @Test
    public void verifyInV32WithAllVerbs() {
        verifyV32("with-all-verbs");
    }

    @Test
    public void verifyInV3WithCallbackOperations() {
        verifyV3("with-callback-operations");
    }
    @Test
    public void verifyInV31WithCallbackOperations() {
        verifyV31("with-callback-operations");
    }
    @Test
    public void verifyInV32WithCallbackOperations() {
        verifyV32("with-callback-operations");
    }

    @Test
    public void verifyInV31WithWebhookOperations() {
        verifyV31("with-webhook-operations");
    }
    @Test
    public void verifyInV32WithWebhookOperations() {
        verifyV32("with-webhook-operations");
    }

    @Test
    public void verifyInV32WithAdditionalOperations() {
        verifyV32("with-additional-operations");
    }

    @Test
    public void verifyInV2Extensive() {
        verifyV2("extensive-api");
    }
    @Test
    public void verifyInV3Extensive() {
        verifyV3("extensive-api");
    }
    @Test
    public void verifyInV31Extensive() {
        verifyV31("extensive-api");
    }
    @Test
    public void verifyInV32Extensive() {
        verifyV32("extensive-api");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR005 - UndefinedWso2ScopeUse - WSO2 scope definition does not exists", RuleType.VULNERABILITY, Severity.BLOCKER, tags("api-manager", "vulnerability", "wso2"));
    }
}
