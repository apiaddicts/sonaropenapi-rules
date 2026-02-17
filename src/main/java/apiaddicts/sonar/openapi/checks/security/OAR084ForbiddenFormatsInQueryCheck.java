package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR084ForbiddenFormatsInQueryCheck.KEY)
public class OAR084ForbiddenFormatsInQueryCheck extends AbstractForbiddenQueryCheck {

    public static final String KEY = "OAR084";
    private static final String MESSAGE = "OAR084.error";

    public OAR084ForbiddenFormatsInQueryCheck() {
        super(KEY, MESSAGE);
        forbiddenItemsStr = "password";
    }

    @Override
    protected void validateParameters(JsonNode params, boolean isV2) {
        params.elements().forEach(param -> {
            JsonNode in = param.get("in");
            if (in == null || !"query".equals(in.getTokenValue())) return;

            JsonNode formatNode = isV2 ? param.get("format") : param.get("schema").get("format");

            if (formatNode != null && !formatNode.isMissing() && !formatNode.isNull() &&
                    forbiddenItems.contains(formatNode.getTokenValue())) {
                addIssue(KEY, translate(MESSAGE, formatNode.getTokenValue()), formatNode);
            }
        });
    }
}