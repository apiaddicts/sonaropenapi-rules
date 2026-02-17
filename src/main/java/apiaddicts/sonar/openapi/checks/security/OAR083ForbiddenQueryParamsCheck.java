package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Rule(key = OAR083ForbiddenQueryParamsCheck.KEY)
public class OAR083ForbiddenQueryParamsCheck extends AbstractForbiddenQueryCheck {

    public static final String KEY = "OAR083";
    private static final String MESSAGE = "OAR083.error";

    @RuleProperty(
            key = "forbidden-query-params",
            description = "List of forbidden query params separated by comma",
            defaultValue = "email, password"
    )
    public String forbiddenQueryParamsStr = "email, password";

    private final Set<String> forbiddenQueryParams = new HashSet<>();

    public OAR083ForbiddenQueryParamsCheck() {
        super(KEY, MESSAGE);
    }

    @Override
    protected void validateParameters(JsonNode parametersNode, boolean isV2) {
        if (!forbiddenQueryParamsStr.trim().isEmpty() && forbiddenQueryParams.isEmpty()) {
            forbiddenQueryParams.addAll(Arrays.asList(forbiddenQueryParamsStr.split(",")));
        }

        Set<String> queryParams = parametersNode.elements().stream()
                .map(p -> p.get("name"))
                .filter(n -> n != null && !n.isNull())
                .map(JsonNode::getTokenValue)
                .collect(Collectors.toSet());

        String forbidden = forbiddenQueryParams.stream()
                .filter(queryParams::contains)
                .collect(Collectors.joining(", "));

        if (!forbidden.isEmpty()) {
            addIssue(KEY, translate(MESSAGE, forbidden), parametersNode.key());
        }
    }
}