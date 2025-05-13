package org.sonar.samples.openapi;

import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;

import apiaddicts.sonar.openapi.I18nContext;
import apiaddicts.sonar.openapi.OpenAPICustomRulesDefinition;

import org.apiaddicts.apitools.dosonarapi.api.OpenApiCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseCheckTest {

    private static final Logger LOG = Logger.getGlobal();

    protected static RulesDefinition.Repository repository;

    protected String ruleName;
    protected OpenApiCheck check;
    protected String v2Path;
    protected String v3Path;
    protected String v31Path;

    protected String getV2Path(String prefix) {
        return "src/test/resources/checks/v2/" + prefix + "/" + ruleName + "/";
    }

    protected String getV3Path(String prefix) {
        return "src/test/resources/checks/v3/" + prefix + "/" + ruleName + "/";
    }

    protected String getV31Path(String prefix) {
        return "src/test/resources/checks/v31/" + prefix + "/" + ruleName + "/";
    }

    protected void verifyV2(String file) {
        verify(file, true, false, false);
    }

    protected void verifyV3(String file) {
        verify(file, false, true, false);
    }

    protected void verifyV31(String file) {
        verify(file, false, false, true);
    }

    private void verify(String file, boolean isV2, boolean isV3, boolean isV31) {
        String filePath;
        if (isV2) {
            filePath = v2Path + file;
        } else if (isV31) {
            filePath = v31Path + file;
        } else if (isV3) {
            filePath = v3Path + file;
        } else {
            throw new IllegalArgumentException("At least one version flag must be set to true.");
        }

        if (filePath.contains(".")) {
            LOG.info("Testing file : " + filePath);
            ExtendedOpenApiCheckVerifier.verify(filePath, check, isV2, isV3, isV31);
        } else {
            LOG.info("Testing file : " + filePath + ".yaml");
            ExtendedOpenApiCheckVerifier.verify(filePath + ".yaml", check, isV2, isV3, isV31);
            LOG.info("Testing file : " + filePath + ".json");
            ExtendedOpenApiCheckVerifier.verify(filePath + ".json", check, isV2, isV3, isV31);
        }
    }

    @BeforeClass
    public static void beforeClass() {
        I18nContext.setLang("en");
        if (repository != null) return;
        OpenAPICustomRulesDefinition rulesDefinition = new OpenAPICustomRulesDefinition();
        RulesDefinition.Context context = new RulesDefinition.Context();
        rulesDefinition.define(context);
        repository = context.repository(OpenAPICustomRulesDefinition.REPOSITORY_KEY);
    }

    @Test
    public abstract void verifyRule();

    @Test
    public void verifyParameters() {
        assertNumberOfParameters(0);
    }

    protected void assertRuleProperties(String title, RuleType type, String severity, Set<String> tags) {
        RulesDefinition.Rule rule = repository.rule(ruleName);
        assertThat(rule).isNotNull();
        assertThat(rule.name()).isEqualTo(title);
        assertThat(rule.type()).isEqualTo(type);
        assertThat(rule.severity()).isEqualTo(severity);
        assertThat(rule.tags()).isEqualTo(tags);
    }

    protected Set<String> tags(String... tags) {
        return new HashSet<>(Arrays.asList(tags));
    }

    protected void assertParameterProperties(String paramName, String defaultValue, RuleParamType type) {
        RulesDefinition.Param param = repository.rule(ruleName).param(paramName);
        assertThat(param).isNotNull();
        assertThat(param.defaultValue()).isEqualTo(defaultValue);
        assertThat(param.type()).isEqualTo(type);
    }

    protected void assertNumberOfParameters(int numberOfParameters) {
        List<RulesDefinition.Param> params = repository.rule(ruleName).params();
        assertThat(params.size()).isEqualTo(numberOfParameters);
    }
}
