package apiaddicts.sonar.openapi.checks.owasp;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR073RateLimitCheck.KEY)
public class OAR073RateLimitCheck extends AbstractPathResponseCheck {

    public static final String KEY = "OAR073";
    private static final String MESSAGE = "OAR073.error";

    private static final String DEFAULT_PATH_STATUS = "/status";
    private static final String DEFAULT_PATHS = DEFAULT_PATH_STATUS + ", /health-check";
    private static final String DEFAULT_STRATEGY = "/exclude";

    public OAR073RateLimitCheck() {
        super(KEY, MESSAGE, DEFAULT_PATHS, DEFAULT_STRATEGY);
    }

    @Override
    protected void validateOperation(JsonNode node, String currentPath) {
        JsonNode responsesNode = node.get("responses");
        if (responsesNode.get("429").isMissing()) {
            addIssue(ruleKey, translate(messageKey), responsesNode.key());
        }
    }
}