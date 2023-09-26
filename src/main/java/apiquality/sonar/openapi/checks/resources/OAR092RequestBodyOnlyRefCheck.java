package apiquality.sonar.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
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
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())) {
            visitRequestBodyNode(node);
        }
    }

    private void visitRequestBodyNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) { // Iterate through each path
            for (JsonNode operationNode : pathNode.propertyMap().values()) { // Iterate through each operation in the path
                JsonNode requestBody = operationNode.get("requestBody");
                if (requestBody != null) {
                    if (!requestBody.propertyMap().keySet().equals(Collections.singleton("$ref"))) {
                        addIssue(KEY, translate(MESSAGE), requestBody.key());
                    }
                }
            }
        }
    }
}
