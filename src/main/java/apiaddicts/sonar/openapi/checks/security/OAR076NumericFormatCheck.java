package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;

import apiaddicts.sonar.openapi.checks.format.AbstractFormatCheck;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR076NumericFormatCheck.KEY)
public class OAR076NumericFormatCheck extends AbstractFormatCheck {

    public static final String KEY = "OAR076";
    private static final String MESSAGE = "OAR076.error";

    @Override
    public void validate(String type, String format, JsonNode typeNode) {
        boolean isInvalid = false;

        if ("integer".equals(type)) {
            isInvalid = !isValidIntegerFormat(format);
        } else if ("number".equals(type)) {
            isInvalid = !isValidNumberFormat(format);
        }

        if (isInvalid) {
            addIssue(KEY, translate(MESSAGE), typeNode.key());
        }
    }

    private boolean isValidIntegerFormat(String format) {
        return "int32".equals(format) || "int64".equals(format);
    }

    private boolean isValidNumberFormat(String format) {
        return "float".equals(format) || "double".equals(format);
    }
}