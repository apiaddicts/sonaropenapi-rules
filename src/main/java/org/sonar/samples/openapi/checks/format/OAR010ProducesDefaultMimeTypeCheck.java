package org.sonar.samples.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR010ProducesDefaultMimeTypeCheck.KEY)
public class OAR010ProducesDefaultMimeTypeCheck extends AbstractDefaultMimeCheck {

	public static final String KEY = "OAR010";
	private static final String MESSAGE = "generic.produce";

	public OAR010ProducesDefaultMimeTypeCheck() {
		super(KEY,"produces", MESSAGE);
	}
}
