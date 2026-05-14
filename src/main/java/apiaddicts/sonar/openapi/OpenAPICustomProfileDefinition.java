package apiaddicts.sonar.openapi;

import org.apiaddicts.apitools.dosonarapi.api.OpenApiCustomRuleRepository;
import org.apiaddicts.apitools.dosonarapi.checks.CheckList;
import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.RulesLists;

import javax.annotation.Nullable;
import java.util.List;

public class OpenAPICustomProfileDefinition implements BuiltInQualityProfilesDefinition {
	public static final String OPENAPI_WAY = "OpenAPI";

	public OpenAPICustomProfileDefinition() {
		this(null);
	}

	public OpenAPICustomProfileDefinition(@Nullable OpenApiCustomRuleRepository[] repositories) {
		// Intentional blank
	}

	@Override
	public void define(Context context) {
		NewBuiltInQualityProfile yamlProfile = context.createBuiltInQualityProfile(OPENAPI_WAY, "yaml");
		addBaseRules(yamlProfile, CheckList.REPOSITORY_KEY);
		addRepositoryRules(yamlProfile, OpenAPICustomRulesDefinition.REPOSITORY_KEY, RulesLists.getAllChecks());
		yamlProfile.done();

		NewBuiltInQualityProfile jsonProfile = context.createBuiltInQualityProfile(OPENAPI_WAY, "json");
		addBaseRules(jsonProfile, CheckList.JSON_REPOSITORY_KEY);
		addRepositoryRules(jsonProfile, OpenAPICustomRulesDefinition.JSON_REPOSITORY_KEY, RulesLists.getAllChecks());
		jsonProfile.done();
	}

	private void addBaseRules(NewBuiltInQualityProfile profile, String repositoryKey) {
		for (Class<?> check : CheckList.getChecks()) {
			Rule annotation = AnnotationUtils.getAnnotation(check, Rule.class);
			profile.activateRule(repositoryKey, annotation.key());
		}
	}

	private void addRepositoryRules(NewBuiltInQualityProfile profile, String key, List<Class<?>> checks) {
		for (Class<?> check : checks) {
			Rule annotation = AnnotationUtils.getAnnotation(check, Rule.class);
			if (!isTemplateRule(annotation.key())) {
				profile.activateRule(key, annotation.key());
			}
		}
	}

	private boolean isTemplateRule(String ruleKey) {
		return "OAR112".equals(ruleKey);
	}
}
