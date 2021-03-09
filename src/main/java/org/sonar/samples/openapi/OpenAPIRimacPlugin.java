package org.sonar.samples.openapi;

import org.sonar.api.Plugin;

/**
 * Entry point of your plugin containing your custom rules.
 */
public class OpenAPIRimacPlugin implements Plugin {

	@Override
	public void define(Context context) {
		context.addExtensions(
				// server extensions -> objects are instantiated during server start
				OpenAPIRimacProfileDefinition.class,
				OpenAPIRimacRulesDefinition.class,
				// batch extensions -> objects are instantiated during code analysis
				OpenAPIRimacCustomRuleRepository.class
		);
	}

}
