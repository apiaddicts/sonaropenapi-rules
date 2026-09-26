package apiaddicts.sonar.openapi.checks.format;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR007UndefinedResponseMediaTypeCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR007";
        check = new OAR007UndefinedResponseMediaTypeCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyInV2WithDefault() {
        verifyV2("with-default");
    }

    @Test
    public void verifyInV2WithSpecific() {
        verifyV2("with-specific");
    }

    @Test
    public void verifyInV2WithDefaultAndSpecific() {
        verifyV2("with-default-and-specific");
    }

    @Test
    public void verifyInV2WithoutAnything() {
        verifyV2("without-anything");
    }

    @Test
    public void verifyInV2WithSpecificPut() {
        verifyV2("with-specific-put");
    }

    @Test
    public void verifyInV3WithoutAnything() {
        verifyV3("without-anything");
    }
    @Test
    public void verifyInV31WithoutAnything() {
        verifyV31("without-anything");
    }
    @Test
    public void verifyInV32WithoutAnything() {
        verifyV32("without-anything");
    }

    @Test
    public void verifyInV3WithSpecific() {
        verifyV3("with-specific");
    }
    @Test
    public void verifyInV31WithSpecific() {
        verifyV31("with-specific");
    }
    @Test
    public void verifyInV32WithSpecific() {
        verifyV32("with-specific");
    }

    @Test
    public void verifyInV3WithMultipleOperations() {
        verifyV3("with-multiple-operations");
    }
    @Test
    public void verifyInV31WithMultipleOperations() {
        verifyV31("with-multiple-operations");
    }
    @Test
    public void verifyInV32WithMultipleOperations() {
        verifyV32("with-multiple-operations");
    }

    @Test
    public void verifyInV3WithDefaultAndRef() {
        verifyV3("with-default-and-ref");
    }
    @Test
    public void verifyInV31WithDefaultAndRef() {
        verifyV31("with-default-and-ref");
    }
    @Test
    public void verifyInV32WithDefaultAndRef() {
        verifyV32("with-default-and-ref");
    }

    @Test
    public void verifyInV3WithWrongRef() {
        verifyV3("with-wrong-ref");
    }
    @Test
    public void verifyInV31WithWrongRef() {
        verifyV31("with-wrong-ref");
    }
    @Test
    public void verifyInV32WithWrongRef() {
        verifyV32("with-wrong-ref");
    }

    @Test
    public void verifyInV3WithChainedRef() {
        verifyV3("with-chained-ref");
    }
    @Test
    public void verifyInV31WithChainedRef() {
        verifyV31("with-chained-ref");
    }
    @Test
    public void verifyInV32WithChainedRef() {
        verifyV32("with-chained-ref");
    }

    @Test
    public void verifyInV3WithExternalRef() {
        verifyV3("with-external-ref");
    }
    @Test
    public void verifyInV31WithExternalRef() {
        verifyV31("with-external-ref");
    }
    @Test
    public void verifyInV32WithExternalRef() {
        verifyV32("with-external-ref");
    }

    @Test
    public void verifyInV3With204Response() {
        verifyV3("with-204-response");
    }
    @Test
    public void verifyInV31With204Response() {
        verifyV31("with-204-response");
    }
    @Test
    public void verifyInV32With204Response() {
        verifyV32("with-204-response");
    }

    @Test
    public void verifyInV3WithDefaultResponseKey() {
        verifyV3("with-default-response-key");
    }
    @Test
    public void verifyInV31WithDefaultResponseKey() {
        verifyV31("with-default-response-key");
    }
    @Test
    public void verifyInV32WithDefaultResponseKey() {
        verifyV32("with-default-response-key");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR007 - UndefinedResponseMediaType - APIs must define response media types supported by the API", RuleType.BUG, Severity.BLOCKER, tags("format"));
    }
}
