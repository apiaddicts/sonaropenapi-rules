package org.sonar.samples.openapi;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.samples.openapi.checks.RulesLists;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

/**
 * Declare rule metadata in server repository of rules.
 * This allows to list the rules in the page "Rules".
 */
public class OpenAPICustomRulesDefinition implements RulesDefinition {
	public static final String REPOSITORY_KEY = "openapi-custom";
	private static final String REPOSITORY_NAME = "OpenAPI Custom";
	private static final String ROOT_RESOURCE_FOLDER = "org/sonar/l10n/openapi/rules/openapi/";
	private static final String SECURITY_GROUP = "security";
	private static final String FORMAT_GROUP = "format";
	private static final String RESOURCES_GROUP = "resources";
	private static final String PARAMETERS_GROUP = "parameters";
	private static final String CORE_GROUP = "core";

	@Override
	public void define(Context context) {
		I18nContext.initializeFromUserLanguage();
		NewRepository repository = context
				.createRepository(REPOSITORY_KEY, "openapi")
				.setName(REPOSITORY_NAME);
		new RuleMetadataLoader(getPath(SECURITY_GROUP)).addRulesByAnnotatedClass(repository, RulesLists.getSecurityChecks());
		new RuleMetadataLoader(getPath(FORMAT_GROUP)).addRulesByAnnotatedClass(repository, RulesLists.getFormatChecks());
		new RuleMetadataLoader(getPath(RESOURCES_GROUP)).addRulesByAnnotatedClass(repository, RulesLists.getResourcesChecks());
		new RuleMetadataLoader(getPath(PARAMETERS_GROUP)).addRulesByAnnotatedClass(repository, RulesLists.getParametersChecks());
		new RuleMetadataLoader(getPath(CORE_GROUP)).addRulesByAnnotatedClass(repository, RulesLists.getCoreChecks());
		repository.done();
	}

	private String getPath(String group) {
		String lang = I18nContext.getLang();
		String langFolder = ROOT_RESOURCE_FOLDER.replace("l10n", "l10n" + "/" + lang) + group;
		String folder = ROOT_RESOURCE_FOLDER + group;
		boolean langFolderExists = "es".equals(lang);
		if (langFolderExists) return langFolder;
		return folder;
	}
}
