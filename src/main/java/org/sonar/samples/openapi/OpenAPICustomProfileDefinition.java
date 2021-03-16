package org.sonar.samples.openapi;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.OpenApiCustomRuleRepository;
import org.sonar.samples.openapi.checks.RulesLists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Declare a new quality profile that comprises all the custom rules, plus the SonarOpenApi standard rules.
 * <p>
 * This allows to create a built-in profile that extends the Sonar Way profile, and that includes your rules.
 * This profile will automatically inherit any new rule brought in by the core plugin.
 */
public class OpenAPICustomProfileDefinition implements BuiltInQualityProfilesDefinition {
	public static final String MY_COMPANY_WAY = "Custom";
	private final OpenApiCustomRuleRepository[] repositories;

	public OpenAPICustomProfileDefinition() {
		this(null);
	}

	public OpenAPICustomProfileDefinition(@Nullable OpenApiCustomRuleRepository[] repositories) {
		this.repositories = repositories;
	}

	@Override
	public void define(Context context) {
		NewBuiltInQualityProfile profile = context.createBuiltInQualityProfile(MY_COMPANY_WAY, "openapi");
		addRepositoryRules(profile, OpenAPICustomRulesDefinition.REPOSITORY_KEY, RulesLists.getAllChecks());
		profile.done();
	}

	private void addRepositoryRules(NewBuiltInQualityProfile profile, String key, List<Class> checks) {
		for (Class check : checks) {
			Rule annotation = AnnotationUtils.getAnnotation(check, Rule.class);
			profile.activateRule(key, annotation.key());
		}
	}
}
