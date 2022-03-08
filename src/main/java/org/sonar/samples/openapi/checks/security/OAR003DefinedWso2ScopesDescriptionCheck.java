package org.sonar.samples.openapi.checks.security;

import org.sonar.check.Rule;
import org.sonar.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR003DefinedWso2ScopesDescriptionCheck.KEY)
public class OAR003DefinedWso2ScopesDescriptionCheck extends AbstractWso2ScopesCheck {

	public static final String KEY = "OAR003";
	private static final String MESSAGE = "OAR003.error";

	@Override
	protected void visitScope(JsonNode scope) {
		JsonNode description = scope.propertyMap().get("description");
		if (description == null || description.isMissing()) {
			addIssue(KEY, translate(MESSAGE), scope);
		} else if (description.isNull() || description.getTokenValue().trim().equals("")) {
			addIssue(KEY, translate(MESSAGE), description.key());
		}
	}
}
