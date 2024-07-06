package apiquality.sonar.openapi.checks.operations;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiquality.sonar.openapi.checks.BaseCheck;

import java.util.Collections;
import java.util.Set;

@Rule(key = OAR092RequestBodyOnlyRefCheck.KEY)
public class OAR092RequestBodyOnlyRefCheck extends BaseCheck {

    public static final String KEY = "OAR092";
    private static final String MESSAGE = "OAR092.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi3Grammar.PATHS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi3Grammar.PATHS.equals(node.getType()) || OpenApi31Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        }
    }

    private void visitPathsNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) { 
                JsonNode requestBody = operationNode.get("requestBody");
                
                if (requestBody != null && !isMissingNode(requestBody)) {
                    checkRequestBody(requestBody);
                }
            }
        }
    }

    private void checkRequestBody(JsonNode requestBody) {

        if (!requestBody.propertyMap().keySet().equals(Collections.singleton("$ref"))) {
            addIssue(KEY, translate(MESSAGE), requestBody.key());
        }
    }

    private boolean isMissingNode(JsonNode node) {
        return "null".equals(node.getTokenValue());
    }
}