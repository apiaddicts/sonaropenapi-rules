package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR016NumericFormatCheck.KEY)
public class OAR016NumericFormatCheck extends AbstractFormatCheck {

	public static final String KEY = "OAR016";
	private static final String MESSAGE = "OAR016.error";

	@Override
	public void validate(String type, String format, JsonNode typeNode) {
		if (isInvalidInteger(type, format) || isInvalidNumber(type, format)) {
			addIssue(KEY, translate(MESSAGE), typeNode.key());
		}
	}

	private boolean isInvalidInteger(String type, String format) {
		return "integer".equals(type) && format != null && !("int32".equals(format) || "int64".equals(format));
	}

	private boolean isInvalidNumber(String type, String format) {
		return "number".equals(type) && format != null && !("float".equals(format) || "double".equals(format));
	}
}
