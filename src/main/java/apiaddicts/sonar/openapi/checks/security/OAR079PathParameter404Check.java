package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.owasp.AbstractPathResponseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR079PathParameter404Check.KEY)
public class OAR079PathParameter404Check extends AbstractPathResponseCheck {

    public static final String KEY = "OAR079";
    private static final String MESSAGE = "OAR079.error";
    private static final String DEFAULT_PATHS = "/status";
    private static final String DEFAULT_STRATEGY = "/exclude";

    public OAR079PathParameter404Check() {
        super(KEY, MESSAGE, DEFAULT_PATHS, DEFAULT_STRATEGY);
    }

    @Override
    protected void validateOperation(JsonNode node, String currentPath) {
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode == null || !parametersNode.isArray()) return;

        for (JsonNode parameterNode : parametersNode.elements()) {
            JsonNode inNode = parameterNode.get("in");
            if (inNode != null && "path".equals(inNode.getTokenValue())) {
                JsonNode responsesNode = node.get("responses");
                if (responsesNode.get("404").isMissing()) {
                    addIssue(ruleKey, translate(messageKey), responsesNode.key());
                    break;
                }
            }
        }
    }
}