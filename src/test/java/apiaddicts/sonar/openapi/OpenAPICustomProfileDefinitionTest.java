package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
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
		BuiltInQualityProfile profile = context.profile("openapi", OpenAPICustomProfileDefinition.MY_COMPANY_WAY);
		assertThat(profile).isNotNull();
		assertThat(profile.language()).isEqualTo("openapi");
		assertThat(profile.name()).isEqualTo(OpenAPICustomProfileDefinition.MY_COMPANY_WAY);

		assertThat(profile.rules()).hasSize(RulesLists.getAllChecks().size() - 1);
		assertThat(profile.rules().stream().noneMatch(r -> r.ruleKey().equals("OAR112"))).isTrue();
	}
}
