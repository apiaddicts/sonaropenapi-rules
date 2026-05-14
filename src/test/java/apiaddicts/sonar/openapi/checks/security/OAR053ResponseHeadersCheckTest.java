package apiaddicts.sonar.openapi.checks.security;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.apiaddicts.apitools.dosonarapi.api.PreciseIssue;
import apiaddicts.sonar.openapi.BaseCheckTest;
import apiaddicts.sonar.openapi.ExtendedOpenApiCheckVerifier;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OAR053ResponseHeadersCheckTest extends BaseCheckTest{
@Before
        public void init() {
            ruleName = "OAR053";
            check = new OAR053ResponseHeadersCheck();
            v2Path = getV2Path("security");
            v3Path = getV3Path("security");
        v31Path = getV31Path("security");
        v32Path = getV32Path("security");
        }


    @Test
    public void verifyInV2(){
      verifyV2("valid");
    }
     @Test
    public void verifyInV3(){
      verifyV3("valid");
    }
    @Test
    public void verifyInV31() {
        verifyV31("valid");
    }
    @Test
    public void verifyInV32() {
        verifyV32("valid");
    }

    @Test
    public void verifyMissingHeaderInV2() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "missing-header.yaml"), check, true, false, false, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "missing-header.json"), check, true, false, false, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyMissingHeaderInV3() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v3Path + "missing-header.yaml"), check, false, true, false, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v3Path + "missing-header.json"), check, false, true, false, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyMissingHeaderInV31() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v31Path + "missing-header.yaml"), check, false, false, true, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v31Path + "missing-header.json"), check, false, false, true, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyMissingHeaderInV32() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v32Path + "missing-header.yaml"), check, false, false, false, true);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v32Path + "missing-header.json"), check, false, false, false, true);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyForbiddenHeaderInV2() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "forbidden-header.yaml"), check, true, false, false, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v2Path + "forbidden-header.json"), check, true, false, false, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyForbiddenHeaderInV3() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v3Path + "forbidden-header.yaml"), check, false, true, false, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v3Path + "forbidden-header.json"), check, false, true, false, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyForbiddenHeaderInV31() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v31Path + "forbidden-header.yaml"), check, false, false, true, false);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v31Path + "forbidden-header.json"), check, false, false, true, false);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyForbiddenHeaderInV32() {
        List<PreciseIssue> yamlIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v32Path + "forbidden-header.yaml"), check, false, false, false, true);
        assertThat(yamlIssues).isNotEmpty();
        check = new OAR053ResponseHeadersCheck();
        List<PreciseIssue> jsonIssues = ExtendedOpenApiCheckVerifier.scanFileForIssues(
                new File(v32Path + "forbidden-header.json"), check, false, false, false, true);
        assertThat(jsonIssues).isNotEmpty();
    }

    @Test
    public void verifyExcludedPathInV2() {
        verifyV2("excluded-path");
    }

    @Test
    public void verifyExcludedPathInV3() {
        verifyV3("excluded-path");
    }

    @Test
    public void verifyExcludedPathInV31() {
        verifyV31("excluded-path");
    }

    @Test
    public void verifyExcludedPathInV32() {
        verifyV32("excluded-path");
    }

    @Test
    public void verifyExcludedCodeInV2() {
        verifyV2("excluded-code");
    }

    @Test
    public void verifyExcludedCodeInV3() {
        verifyV3("excluded-code");
    }

    @Test
    public void verifyExcludedCodeInV31() {
        verifyV31("excluded-code");
    }

    @Test
    public void verifyExcludedCodeInV32() {
        verifyV32("excluded-code");
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(5);
        assertParameterProperties("mandatory-headers", "X-Trace-ID", RuleParamType.STRING);
        assertParameterProperties("allowed-headers", "idCorrelacion, X-CorrelacionId, X-Global-Trasaction-Id, x-power-by, X-Trace-ID", RuleParamType.STRING);
        assertParameterProperties("included-response-codes", "*", RuleParamType.STRING);
        assertParameterProperties("excluded-response-codes", "204", RuleParamType.STRING);
        assertParameterProperties("path-exclusions", "/status", RuleParamType.STRING);
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR053 - ResponseHeaders - There are mandatory response headers and others that are not allowed", RuleType.VULNERABILITY, Severity.MAJOR, tags("vulnerability"));
    }
}
