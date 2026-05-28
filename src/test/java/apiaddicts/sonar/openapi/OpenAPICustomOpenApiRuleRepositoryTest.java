package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class OpenAPICustomOpenApiRuleRepositoryTest {

	@Test
	public void testRepositoryKey() {
		OpenAPICustomOpenApiRuleRepository repository = new OpenAPICustomOpenApiRuleRepository();
		assertThat(repository.repositoryKey()).isEqualTo("openapi-custom");
	}

	@Test
	public void testCheckClassesNotEmpty() {
		OpenAPICustomOpenApiRuleRepository repository = new OpenAPICustomOpenApiRuleRepository();
		assertThat(repository.checkClasses()).isNotEmpty();
	}

	@Test
	public void testCheckClassesSameAsAllChecks() {
		OpenAPICustomOpenApiRuleRepository repository = new OpenAPICustomOpenApiRuleRepository();
		OpenAPICustomRuleRepository yamlRepo = new OpenAPICustomRuleRepository();
		assertThat(repository.checkClasses()).isEqualTo(yamlRepo.checkClasses());
	}
}
