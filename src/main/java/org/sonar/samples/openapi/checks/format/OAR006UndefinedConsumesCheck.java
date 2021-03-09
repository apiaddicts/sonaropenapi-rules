package org.sonar.samples.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR006UndefinedConsumesCheck.KEY)
public class OAR006UndefinedConsumesCheck extends AbstractUndefinedMimeCheck {

	public static final String KEY = "OAR006";

	public OAR006UndefinedConsumesCheck() {
		super(KEY, "consumes");
	}
}
