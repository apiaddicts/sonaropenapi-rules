package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;

import apiaddicts.sonar.openapi.I18nContext;
import apiaddicts.sonar.openapi.OpenAPICustomRulesDefinition;
import apiaddicts.sonar.openapi.checks.RulesLists;

public class OpenAPICustomRulesDefinitionTest {

	@Test
	public void testRepository() {
		I18nContext.setLang("en");
		OpenAPICustomRulesDefinition rulesDefinition = new OpenAPICustomRulesDefinition();
		RulesDefinition.Context context = new RulesDefinition.Context();
		rulesDefinition.define(context);
		Repository repository = context.repository(OpenAPICustomRulesDefinition.REPOSITORY_KEY);
		assertThat(repository.name()).isEqualTo("OpenAPI Custom");
		assertThat(repository.language()).isEqualTo("openapi");
		assertThat(repository.rules()).hasSize(RulesLists.getAllChecks().size());
	}
}
