package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR083ForbiddenQueryParamsCheck.KEY)
public class OAR083ForbiddenQueryParamsCheck extends AbstractForbiddenQueryCheck {

    public static final String KEY = "OAR083";
    private static final String MESSAGE = "OAR083.error";

    public OAR083ForbiddenQueryParamsCheck() {
        super(KEY, MESSAGE);
        forbiddenItemsStr = "email, password";
    }

    @Override
    protected void validateParameters(JsonNode parametersNode, boolean isV2) {
        Set<String> queryParams = parametersNode.elements().stream()
                .map(p -> p.get("name"))
                .filter(n -> n != null && !n.isNull())
                .map(JsonNode::getTokenValue)
                .collect(Collectors.toSet());

        String forbidden = forbiddenItems.stream()
                .filter(queryParams::contains)
                .collect(Collectors.joining(", "));

        if (!forbidden.isEmpty()) {
            addIssue(KEY, translate(MESSAGE, forbidden), parametersNode.key());
        }
    }
}