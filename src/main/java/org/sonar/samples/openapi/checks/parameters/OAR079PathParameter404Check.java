package org.sonar.samples.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.samples.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import java.util.Set;

@Rule(key = OAR079PathParameter404Check.KEY)
public class OAR079PathParameter404Check extends BaseCheck {

    public static final String KEY = "OAR079";
    private static final String MESSAGE = "OAR079.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitOperationNode(node);
    }

    private void visitOperationNode(JsonNode node) {
        JsonNode responsesNode= node.get("responses");
        JsonNode parametersNode = node.get("parameters");
        if (parametersNode != null && parametersNode.isArray()) {
            for (JsonNode parameterNode : parametersNode.elements()) {
                if (isPathParameter(parameterNode)) {
                    JsonNode responsesNode404 = node.get("responses").get("404");
                    if (responsesNode404.isMissing()) {
                        addIssue(KEY, translate(MESSAGE), responsesNode.key());
                        break; 
                    }
                }
            }
        }
    }

    private boolean isPathParameter(JsonNode parameterNode) {
        JsonNode inNode = parameterNode.get("in");
        return inNode != null && "path".equals(inNode.getTokenValue());
    }
}