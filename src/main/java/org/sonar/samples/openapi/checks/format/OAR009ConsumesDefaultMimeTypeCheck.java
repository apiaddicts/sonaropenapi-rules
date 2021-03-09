package org.sonar.samples.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR009ConsumesDefaultMimeTypeCheck.KEY)
public class OAR009ConsumesDefaultMimeTypeCheck extends AbstractDefaultMimeCheck {

	public static final String KEY = "OAR009";
	private static final String MESSAGE = "generic.consume";

	public OAR009ConsumesDefaultMimeTypeCheck() {
		super(KEY, "consumes", MESSAGE);
	}
}
