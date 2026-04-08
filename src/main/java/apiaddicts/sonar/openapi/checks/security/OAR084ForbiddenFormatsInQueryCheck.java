package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

@Rule(key = OAR084ForbiddenFormatsInQueryCheck.KEY)
public class OAR084ForbiddenFormatsInQueryCheck extends AbstractForbiddenQueryCheck {

    public static final String KEY = "OAR084";
    private static final String MESSAGE = "OAR084.error";

    @RuleProperty(
            key = "forbidden-query-formats",
            description = "List of forbidden query formats separated by comma",
            defaultValue = "password"
    )
    public String forbiddenQueryFormatsStr = "password";

    private final Set<String> forbiddenQueryFormats = new HashSet<>();

    public OAR084ForbiddenFormatsInQueryCheck() {
        super(KEY, MESSAGE);
    }

    @Override
    protected void validateParameters(JsonNode parametersNode, boolean isV2) {
        if (!forbiddenQueryFormatsStr.trim().isEmpty() && forbiddenQueryFormats.isEmpty()) {
            forbiddenQueryFormats.addAll(Arrays.asList(forbiddenQueryFormatsStr.split(",")));
        }

        parametersNode.elements().forEach(param -> {
            JsonNode in = param.get("in");
            if (!"query".equals(in.getTokenValue())) return;

            JsonNode formatNode = isV2 ? param.get("format") : param.get("schema").get("format");

            if (!formatNode.isMissing() && !formatNode.isNull() &&
                    forbiddenQueryFormats.contains(formatNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE, formatNode.getTokenValue()), formatNode);
            }
        });
    }
}