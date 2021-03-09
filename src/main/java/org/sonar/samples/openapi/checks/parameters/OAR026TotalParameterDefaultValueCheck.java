package org.sonar.samples.openapi.checks.parameters;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Set;

@Rule(key = OAR026TotalParameterDefaultValueCheck.KEY)
public class OAR026TotalParameterDefaultValueCheck extends BaseCheck {

    protected static final String KEY = "OAR026";
    private static final String MESSAGE = "OAR026.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitV2Node(node);
    }

    private void visitV2Node(JsonNode node) {
        if (!"$total".equals(node.get("name").getTokenValue())) return;
        JsonNode defaultNode = ( node.getType() == OpenApi2Grammar.PARAMETER ) ? node.get("default") : node.at("/schema/default");
        if (defaultNode.isMissing()) {
            if (node.key().isMissing()) {
                addIssue(KEY, translate(MESSAGE), node);
            } else {
                addIssue(KEY, translate(MESSAGE), node.key());
            }
        } else if (!"false".equals(defaultNode.getTokenValue())) {
            addIssue(KEY, translate(MESSAGE), defaultNode);
        }
    }
}