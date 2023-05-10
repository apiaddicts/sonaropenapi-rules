package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.samples.openapi.checks.BaseCheck;

import java.util.Set;

@Rule(key = OAR073RateLimitCheck.KEY)
public class OAR073RateLimitCheck extends BaseCheck {

    public static final String KEY = "OAR073";
    private static final String MESSAGE = "OAR073.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitOperationNode(node);
    }

    private void visitOperationNode(JsonNode node) {
        JsonNode responsesNode = node.get("responses");
        if (responsesNode != null) {
            JsonNode node429 = responsesNode.get("429");
            if (node429.isMissing()) {
                addIssue(KEY, translate(MESSAGE), responsesNode.key());
            }
        }
    }
}
