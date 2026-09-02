package apiaddicts.sonar.openapi.checks.format;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import apiaddicts.sonar.openapi.BaseCheckTest;

public class OAR116PathPatternCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR116";
        check = new OAR116PathPatternCheck();
        v2Path = getV2Path("format");
        v3Path = getV3Path("format");
        v31Path = getV31Path("format");
        v32Path = getV32Path("format");
    }

    @Test
    public void verifyValidV2() {
        verifyV2("valid");
    }
    @Test
    public void verifyValidV3() {
        verifyV3("valid");
    }
    @Test
    public void verifyValidV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyValidV32() {
        verifyV32("valid");
    }

    @Test
    public void verifyInvalidV2() throws Exception {
        setPattern("^/v[0-9]+");
        verifyV2("invalid.yaml");
    }
    @Test
    public void verifyInvalidV3() throws Exception {
        setPattern("^/v[0-9]+");
        verifyV3("invalid.yaml");
    }
    @Test
    public void verifyInvalidV31() throws Exception {
        setPattern("^/v[0-9]+");
        verifyV31("invalid.yaml");
    }
    @Test
    public void verifyInvalidV32() throws Exception {
        setPattern("^/v[0-9]+");
        verifyV32("invalid.yaml");
    }
    @Test
    public void verifyNullPatternFallsBackToDefault() throws Exception {
        setPattern(null);
        verifyV3("valid");
    }

    private void setPattern(String pattern) throws Exception {
        Field field = OAR116PathPatternCheck.class.getDeclaredField("patternStr");
        field.setAccessible(true);
        field.set(check, pattern);
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("pattern", "^/", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR116 - PathPattern - All API paths must match the configured regular expression", RuleType.BUG, Severity.MAJOR, tags("format"));
    }
}
