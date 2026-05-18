package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.apiaddicts.apitools.dosonarapi.checks.CheckList;
import org.junit.Test;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition.BuiltInQualityProfile;

import apiaddicts.sonar.openapi.checks.RulesLists;

public class OpenAPICustomProfileDefinitionTest {

	@Test
	public void testDefine() {
		I18nContext.setLang("en");
		OpenAPICustomProfileDefinition profileDefinition = new OpenAPICustomProfileDefinition();
		BuiltInQualityProfilesDefinition.Context context = new BuiltInQualityProfilesDefinition.Context();
		profileDefinition.define(context);

		int expectedSize = CheckList.getChecks().size() + RulesLists.getAllChecks().size() - 1;

		BuiltInQualityProfile yamlProfile = context.profile("yaml", OpenAPICustomProfileDefinition.OPENAPI_WAY);
		assertThat(yamlProfile).isNotNull();
		assertThat(yamlProfile.language()).isEqualTo("yaml");
		assertThat(yamlProfile.name()).isEqualTo(OpenAPICustomProfileDefinition.OPENAPI_WAY);
		assertThat(yamlProfile.rules()).hasSize(expectedSize);
		assertThat(yamlProfile.rules().stream().noneMatch(r -> r.ruleKey().equals("OAR112"))).isTrue();

		BuiltInQualityProfile jsonProfile = context.profile("json", OpenAPICustomProfileDefinition.OPENAPI_WAY);
		assertThat(jsonProfile).isNotNull();
		assertThat(jsonProfile.language()).isEqualTo("json");
		assertThat(jsonProfile.name()).isEqualTo(OpenAPICustomProfileDefinition.OPENAPI_WAY);
		assertThat(jsonProfile.rules()).hasSize(expectedSize);
		assertThat(jsonProfile.rules().stream().noneMatch(r -> r.ruleKey().equals("OAR112"))).isTrue();
	}
}
