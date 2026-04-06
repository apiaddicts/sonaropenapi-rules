package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import apiaddicts.sonar.openapi.checks.RulesLists;

public class OpenAPICustomRuleRepositoryTest {

	@Test
	public void testRepositoryKey() {
		OpenAPICustomRuleRepository repository = new OpenAPICustomRuleRepository();
		assertThat(repository.repositoryKey()).isEqualTo(OpenAPICustomRulesDefinition.REPOSITORY_KEY);
	}

	@Test
	public void testCheckClasses() {
		OpenAPICustomRuleRepository repository = new OpenAPICustomRuleRepository();
		assertThat(repository.checkClasses()).isEqualTo(RulesLists.getAllChecks());
		assertThat(repository.checkClasses()).isNotEmpty();
	}
}
