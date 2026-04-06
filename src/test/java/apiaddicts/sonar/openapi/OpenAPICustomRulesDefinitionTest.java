package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import java.lang.reflect.Method;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;

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

	@Test
	public void testGetPathWithSpanishLanguage() throws Exception {
		I18nContext.setLang("es");
		OpenAPICustomRulesDefinition rulesDefinition = new OpenAPICustomRulesDefinition();

		Method method = OpenAPICustomRulesDefinition.class.getDeclaredMethod("getPath", String.class);
		method.setAccessible(true);
		String path = (String) method.invoke(rulesDefinition, "security");

		assertThat(path).contains("/es/");
	}

	@Test
	public void testMarkAsTemplateWithNonExistentRule() throws Exception {
		I18nContext.setLang("en");
		OpenAPICustomRulesDefinition rulesDefinition = new OpenAPICustomRulesDefinition();
		RulesDefinition.Context context = new RulesDefinition.Context();
		RulesDefinition.NewRepository newRepo = context.createRepository("test-repo", "openapi").setName("Test");

		Method method = OpenAPICustomRulesDefinition.class.getDeclaredMethod(
				"markAsTemplate", RulesDefinition.NewRepository.class, String.class);
		method.setAccessible(true);
		method.invoke(rulesDefinition, newRepo, "NON_EXISTENT_RULE");

		newRepo.done();
		assertThat(context.repository("test-repo").rules()).isEmpty();
	}
}
