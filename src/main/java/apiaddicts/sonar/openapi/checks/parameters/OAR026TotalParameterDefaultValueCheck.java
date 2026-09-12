package apiaddicts.sonar.openapi.checks.parameters;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import apiaddicts.sonar.openapi.utils.ExternalRefHandler;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

@Rule(key = OAR026TotalParameterDefaultValueCheck.KEY)
public class OAR026TotalParameterDefaultValueCheck extends BaseCheck {

    protected static final String KEY = "OAR026";
    private static final String MESSAGE = "OAR026.error";
    private static final String TOTAL_PARAM_NAME = "$total";

    private final ExternalRefHandler handleExternalRef = new ExternalRefHandler();

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION,
                OpenApi31Grammar.OPERATION, OpenApi32Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (!"get".equals(node.key().getTokenValue())) {
            return;
        }

        JsonNode parametersNode = node.get("parameters");
        if (parametersNode.isMissing() || !parametersNode.isArray()) {
            return;
        }

        for (JsonNode parameterElement : parametersNode.elements()) {
            handleExternalRef.resolve(parameterElement, this::checkTotalParameter);
        }
    }

    private void checkTotalParameter(JsonNode resolved) {
        if (resolved.isMissing()) return;

        JsonNode inNode = resolved.get("in");
        if (inNode.isMissing() || !"query".equals(inNode.getTokenValue())) return;

        JsonNode nameNode = resolved.get("name");
        if (nameNode.isMissing() || !TOTAL_PARAM_NAME.equals(nameNode.getTokenValue())) return;

        JsonNode defaultNode = (resolved.getType() == OpenApi2Grammar.PARAMETER)
                ? resolved.get("default")
                : resolved.at("/schema/default");

        if (defaultNode.isMissing()) {
            return;
        }

        if (!"false".equals(defaultNode.getTokenValue())) {
            addIssue(KEY, translate(MESSAGE), handleExternalRef.getTrueNode(defaultNode));
        }
    }
}
