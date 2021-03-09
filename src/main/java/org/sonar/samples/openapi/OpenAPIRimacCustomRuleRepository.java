package org.sonar.samples.openapi;

import org.sonar.api.batch.ScannerSide;
import org.sonar.plugins.openapi.api.OpenApiCustomRuleRepository;
import org.sonar.samples.openapi.checks.RulesLists;
import org.sonarsource.api.sonarlint.SonarLintSide;

import java.util.List;

import static org.sonar.samples.openapi.OpenAPIRimacRulesDefinition.REPOSITORY_KEY;

/**
 * Makes the rules visible to the OpenAPI scanner sensor,
 * hence adds to the classes that are going to be executed during source code analysis.
 * <p>
 * This class is a batch extension by implementing the {@link OpenApiCustomRuleRepository}
 */
@SonarLintSide
@ScannerSide
public class OpenAPIRimacCustomRuleRepository implements OpenApiCustomRuleRepository {
	@Override
	public String repositoryKey() {
		return REPOSITORY_KEY;
	}

	@Override
	public List<Class> checkClasses() {
		return RulesLists.getAllChecks();
	}
}
