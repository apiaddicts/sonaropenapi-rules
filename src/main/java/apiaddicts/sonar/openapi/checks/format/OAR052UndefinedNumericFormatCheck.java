package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR052UndefinedNumericFormatCheck.KEY)
public class OAR052UndefinedNumericFormatCheck extends AbstractFormatCheck {

	public static final String KEY = "OAR052";
	private static final String MESSAGE = "OAR052.error";

	@Override
	public void validate(String type, String format, JsonNode typeNode) {
		if (("integer".equals(type) || "number".equals(type)) && format == null) {
			addIssue(KEY, translate(MESSAGE), typeNode.key());
		}
	}
}
