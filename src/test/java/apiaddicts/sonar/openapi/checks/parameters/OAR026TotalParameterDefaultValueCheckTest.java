package apiaddicts.sonar.openapi.checks.parameters;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR026TotalParameterDefaultValueCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR026";
        check = new OAR026TotalParameterDefaultValueCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
        v31Path = getV31Path("parameters");
        v32Path = getV32Path("parameters");
    }

    @Test
    public void verifyInV2With$totalWithDefvalFalse() {
        verifyV2("plain-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV2With$totalWithDefvalTrue() {
        verifyV2("plain-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV2With$totalWithoutDefval() {
        verifyV2("plain-with-$total-without-defval");
    }

    @Test
    public void verifyInV2Without$total() {
        verifyV2("plain-without-$total");
    }

    @Test
    public void verifyInV2With$refWith$totalWithDefvalFalse() {
        verifyV2("with-$ref-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV2With$refWith$totalWithDefvalTrue() {
        verifyV2("with-$ref-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV2With$refWith$totalWithoutDefval() {
        verifyV2("with-$ref-with-$total-without-defval");
    }

    @Test
    public void verifyInV2With$refWithout$total() {
        verifyV2("with-$ref-without-$total");
    }

    @Test
    public void verifyInV2WithoutParameters() {
        verifyV2("without-parameters");
    }

    @Test
    public void verifyInV2PostOperationWith$total() {
        verifyV2("post-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV2HeaderParamWith$total() {
        verifyV2("header-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV2With$refUnreferencedWith$total() {
        verifyV2("with-$ref-unreferenced-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV3With$totalWithDefvalFalse() {
        verifyV3("plain-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV3With$totalWithDefvalTrue() {
        verifyV3("plain-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV3With$totalWithoutDefval() {
        verifyV3("plain-with-$total-without-defval");
    }

    @Test
    public void verifyInV3Without$total() {
        verifyV3("plain-without-$total");
    }

    @Test
    public void verifyInV3With$refWith$totalWithDefvalFalse() {
        verifyV3("with-$ref-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV3With$refWith$totalWithDefvalTrue() {
        verifyV3("with-$ref-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV3With$refWith$totalWithoutDefval() {
        verifyV3("with-$ref-with-$total-without-defval");
    }

    @Test
    public void verifyInV3With$refWithout$total() {
        verifyV3("with-$ref-without-$total");
    }

    @Test
    public void verifyInV3WithoutParameters() {
        verifyV3("without-parameters");
    }

    @Test
    public void verifyInV3PostOperationWith$total() {
        verifyV3("post-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV3HeaderParamWith$total() {
        verifyV3("header-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV3With$refUnreferencedWith$total() {
        verifyV3("with-$ref-unreferenced-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV31With$totalWithDefvalFalse() {
        verifyV31("plain-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV31With$totalWithDefvalTrue() {
        verifyV31("plain-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV31With$totalWithoutDefval() {
        verifyV31("plain-with-$total-without-defval");
    }

    @Test
    public void verifyInV31Without$total() {
        verifyV31("plain-without-$total");
    }

    @Test
    public void verifyInV31With$refWith$totalWithDefvalFalse() {
        verifyV31("with-$ref-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV31With$refWith$totalWithDefvalTrue() {
        verifyV31("with-$ref-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV31With$refWith$totalWithoutDefval() {
        verifyV31("with-$ref-with-$total-without-defval");
    }

    @Test
    public void verifyInV31With$refWithout$total() {
        verifyV31("with-$ref-without-$total");
    }

    @Test
    public void verifyInV31WithoutParameters() {
        verifyV31("without-parameters");
    }

    @Test
    public void verifyInV31PostOperationWith$total() {
        verifyV31("post-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV31HeaderParamWith$total() {
        verifyV31("header-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV31With$refUnreferencedWith$total() {
        verifyV31("with-$ref-unreferenced-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV32With$totalWithDefvalFalse() {
        verifyV32("plain-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV32With$totalWithDefvalTrue() {
        verifyV32("plain-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV32With$totalWithoutDefval() {
        verifyV32("plain-with-$total-without-defval");
    }

    @Test
    public void verifyInV32Without$total() {
        verifyV32("plain-without-$total");
    }

    @Test
    public void verifyInV32With$refWith$totalWithDefvalFalse() {
        verifyV32("with-$ref-with-$total-with-defval-false");
    }

    @Test
    public void verifyInV32With$refWith$totalWithDefvalTrue() {
        verifyV32("with-$ref-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV32With$refWith$totalWithoutDefval() {
        verifyV32("with-$ref-with-$total-without-defval");
    }

    @Test
    public void verifyInV32With$refWithout$total() {
        verifyV32("with-$ref-without-$total");
    }

    @Test
    public void verifyInV32WithoutParameters() {
        verifyV32("without-parameters");
    }

    @Test
    public void verifyInV32PostOperationWith$total() {
        verifyV32("post-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV32HeaderParamWith$total() {
        verifyV32("header-with-$total-with-defval-true");
    }

    @Test
    public void verifyInV32With$refUnreferencedWith$total() {
        verifyV32("with-$ref-unreferenced-with-$total-with-defval-true");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR026 - TotalParameterDefaultValue - The $total parameter default value should be false", RuleType.BUG, Severity.CRITICAL, tags("parameters"));
    }

}
