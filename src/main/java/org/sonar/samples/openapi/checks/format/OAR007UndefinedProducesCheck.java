package org.sonar.samples.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR007UndefinedProducesCheck.KEY)
public class OAR007UndefinedProducesCheck extends AbstractUndefinedMimeCheck {

	public static final String KEY = "OAR007";

	public OAR007UndefinedProducesCheck() {
		super(KEY, "produces");
	}
}
