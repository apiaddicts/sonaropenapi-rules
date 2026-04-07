package apiaddicts.sonar.openapi;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.SonarEdition;
import org.sonar.api.SonarProduct;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.SonarRuntime;
import org.sonar.api.utils.Version;

public class OpenAPICustomPluginTest {

	@Test
	public void testDefine() {
		OpenAPICustomPlugin plugin = new OpenAPICustomPlugin();
		SonarRuntime runtime = new SonarRuntime() {
			@Override public Version getApiVersion() { return Version.create(9, 9); }
			@Override public SonarProduct getProduct() { return SonarProduct.SONARQUBE; }
			@Override public SonarQubeSide getSonarQubeSide() { return SonarQubeSide.SERVER; }
			@Override public SonarEdition getEdition() { return SonarEdition.COMMUNITY; }
		};
		Plugin.Context context = new Plugin.Context(runtime);
		plugin.define(context);
		assertThat(context.getExtensions()).hasSize(3);
		assertThat(context.getExtensions()).contains(
				OpenAPICustomProfileDefinition.class,
				OpenAPICustomRulesDefinition.class,
				OpenAPICustomRuleRepository.class);
	}
}
