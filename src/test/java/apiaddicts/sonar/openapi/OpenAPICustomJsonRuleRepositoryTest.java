package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import apiaddicts.sonar.openapi.checks.RulesLists;

public class OpenAPICustomJsonRuleRepositoryTest {

	@Test
	public void testRepositoryKey() {
		OpenAPICustomJsonRuleRepository repository = new OpenAPICustomJsonRuleRepository();
		assertThat(repository.repositoryKey()).isEqualTo(OpenAPICustomRulesDefinition.JSON_REPOSITORY_KEY);
	}

	@Test
	public void testCheckClasses() {
		OpenAPICustomJsonRuleRepository repository = new OpenAPICustomJsonRuleRepository();
		assertThat(repository.checkClasses()).isEqualTo(RulesLists.getAllChecks());
		assertThat(repository.checkClasses()).isNotEmpty();
	}
}
