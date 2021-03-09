package org.sonar.samples.openapi.checks.format;

import org.sonar.check.Rule;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Rule(key = OAR037StringFormatCheck.KEY)
public class OAR037StringFormatCheck extends AbstractFormatCheck {

    public static final String KEY = "OAR037";
    private static final String MESSAGE = "OAR037.error";

    private final Set<String> validFormats = new HashSet<>(
            Arrays.asList("date", "date-time", "password", "byte", "binary", "email", "uuid", "uri", "hostname", "ipv4", "ipv6")
    );

    @Override
    public void validate(String type, String format, JsonNode typeNode) {
        if (isInvalidString(type, format)) {
            addIssue(KEY, translate(MESSAGE), typeNode.key());
        }
    }

    private boolean isInvalidString(String type, String format) {
        return "string".equals(type) && (format != null && !validFormats.contains(format));
    }
}
