package apiaddicts.sonar.openapi.checks.parameters;

import java.lang.reflect.Field;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;
import apiaddicts.sonar.openapi.I18nContext;
import apiaddicts.sonar.openapi.checks.BaseCheck;

public class OAR028FilterParameterCheckTest extends BaseCheckTest {

    @BeforeClass
    public static void setupLang() throws Exception {
        I18nContext.setLang("en");
        Field field = BaseCheck.class.getDeclaredField("resourceBundle");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Before
    public void init() {
        ruleName = "OAR028";
        check = new OAR028FilterParameterCheck();
        v2Path = getV2Path("parameters");
        v3Path = getV3Path("parameters");
        v31Path = getV31Path("parameters");
        v32Path = getV32Path("parameters");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV2Excluded() {
        verifyV2("excluded");
    }

    @Test
    public void verifyInV2Without() {
        verifyV2("plain-without");
    }
    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }
    @Test
    public void verifyInV31() {
        verifyV31("plain");
    }
    @Test
    public void verifyInV32() {
        verifyV32("plain");
    }

    @Test
    public void verifyInV3Excluded() {
        verifyV3("excluded");
    }
    @Test
    public void verifyInV31Excluded() {
        verifyV31("excluded");
    }
    @Test
    public void verifyInV32Excluded() {
        verifyV32("excluded");
    }

    @Test
    public void verifyInV3Without() {
        verifyV3("plain-without");
    }
    @Test
    public void verifyInV31Without() {
        verifyV31("plain-without");
    }
    @Test
    public void verifyInV32Without() {
        verifyV32("plain-without");
    }

    @Test
    public void verifyInV2ExcludeStrategy() {
        setField("pathCheckStrategy", "/exclude");
        setField("pathsStr", "/examples");
        verifyV2("exclude-noncompliant");
    }

    @Test
    public void verifyInV3ExcludeStrategy() {
        setField("pathCheckStrategy", "/exclude");
        setField("pathsStr", "/examples");
        verifyV3("exclude-noncompliant");
    }
    @Test
    public void verifyInV31ExcludeStrategy() {
        verifyV31("exclude-noncompliant");
    }
    @Test
    public void verifyInV32ExcludeStrategy() {
        verifyV32("exclude-noncompliant");
    }

    @Test
    public void verifyInV2EmptyPaths() {
        setField("pathsStr", "");
        verifyV2("plain");
    }

    @Test
    public void verifyInV3EmptyPaths() {
        setField("pathsStr", "");
        verifyV3("plain");
    }
    @Test
    public void verifyInV31EmptyPaths() {
        verifyV31("plain");
    }
    @Test
    public void verifyInV32EmptyPaths() {
        verifyV32("plain");
    }

    @Test
    public void verifyInV3ComponentsParam() {
        verifyV3("components-param");
    }
    @Test
    public void verifyInV31ComponentsParam() {
        verifyV31("components-param");
    }
    @Test
    public void verifyInV32ComponentsParam() {
        verifyV32("components-param");
    }

    private void setField(String name, String value) {
        try {
            java.lang.reflect.Field f = OAR028FilterParameterCheck.class.getDeclaredField(name);
            f.setAccessible(true);
            f.set(check, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR028 - FilterParameter - the chosen parameter must be defined in this operation", RuleType.BUG, Severity.MINOR, tags("parameters"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(3);
        assertParameterProperties("paths", "/examples", RuleParamType.STRING);
        assertParameterProperties("pathValidationStrategy", "/include", RuleParamType.STRING);
        assertParameterProperties("parameterName", "$filter", RuleParamType.STRING);
    }
}