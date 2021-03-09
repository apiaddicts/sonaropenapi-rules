package org.sonar.samples.openapi;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.samples.openapi.checks.RulesLists;

import static org.assertj.core.api.Assertions.assertThat;

public class OpenAPIRimacRulesDefinitionTest {

	@Test
	public void testRepository() {
		I18nContext.setLang("en");
		OpenAPIRimacRulesDefinition rulesDefinition = new OpenAPIRimacRulesDefinition();
		RulesDefinition.Context context = new RulesDefinition.Context();
		rulesDefinition.define(context);
		Repository repository = context.repository(OpenAPIRimacRulesDefinition.REPOSITORY_KEY);
		assertThat(repository.name()).isEqualTo("OpenAPI Rimac");
		assertThat(repository.language()).isEqualTo("openapi");
		assertThat(repository.rules()).hasSize(RulesLists.getAllChecks().size());
	}
}
