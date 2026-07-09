package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR037StringFormatCheck.KEY)
public class OAR037StringFormatCheck extends AbstractFormatCheck {

    public static final String KEY = "OAR037";
    private static final String MESSAGE = "OAR037.error";
    private static final String DEFAULT_FORMATS_ALLOWED = "date,date-time,password,byte,binary,email,uuid,uri,hostname,ipv4,ipv6,HEX,HEX(16),json,xml,base64";

    @RuleProperty(
            key = "formats-allowed",
            description = "List of allowed formats for string types (separated by comma)",
            defaultValue = DEFAULT_FORMATS_ALLOWED
    )
    private String formatsAllowed = DEFAULT_FORMATS_ALLOWED;

    @Override
    public void validate(String type, String format, JsonNode typeNode, JsonNode node) {
        if (!"string".equals(type)) {
            return;
        }

        if (format != null) {
            Set<String> validFormats = Stream.of(formatsAllowed.split(","))
                    .map(f -> f.trim().toLowerCase())
                    .collect(Collectors.toSet());
            if (!validFormats.contains(format.toLowerCase())) {
                addIssue(KEY, translate(MESSAGE), typeNode.key());
            }
            return;
        }

        if (!hasValidPattern(node)) {
            addIssue(KEY, translate(MESSAGE), typeNode.key());
        }
    }

    private boolean hasValidPattern(JsonNode node) {
        JsonNode patternNode = node.get("pattern");
        if (patternNode.isMissing()) {
            return false;
        }
        String pattern = patternNode.getTokenValue();
        if (pattern == null || pattern.isBlank()) {
            return false;
        }
        return isValidRegex(pattern.trim());
    }

    private boolean isValidRegex(String pattern) {
        try {
            Pattern.compile(pattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
