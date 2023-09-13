package org.sonar.samples.openapi.checks.format;

import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import com.google.common.collect.ImmutableSet;

@Rule(key = OAR087SummaryFormatCheck.KEY)
public class OAR087SummaryFormatCheck extends BaseCheck {

    public static final String KEY = "OAR087";
    private static final String MESSAGE = "OAR087.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType())) {
            checkInfoSummary(node);
            checkDefinitionsSummary(node);
        }
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        }
    }

    private void checkInfoSummary(JsonNode rootNode) {
        JsonNode infoNode = rootNode.get("info");
        if (infoNode != null) {
            checkSummaryFormat(infoNode.get("summary"));
        }
    }

    private void checkDefinitionsSummary(JsonNode rootNode) {
        JsonNode definitionsNode = rootNode.get("definitions");
        if (definitionsNode != null) {
            for (JsonNode definition : definitionsNode.propertyMap().values()) {
                checkSummaryFormat(definition.get("summary"));
            }
        }
        
        // For OpenAPI 3.0's 'components/schemas'
        JsonNode componentsNode = rootNode.get("components");
        if (componentsNode != null) {
            JsonNode schemasNode = componentsNode.get("schemas");
            if (schemasNode != null) {
                for (JsonNode schema : schemasNode.propertyMap().values()) {
                    checkSummaryFormat(schema.get("summary"));
                }
            }
        }
    }

    protected void visitPathsNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                checkSummaryFormat(operationNode.get("summary"));
            }
        }
    }

    private void checkSummaryFormat(JsonNode summaryNode) {
        if (summaryNode != null && !summaryNode.isMissing()) {
            String summary = summaryNode.getTokenValue();
            if (!summary.startsWith(Character.toString(summary.charAt(0)).toUpperCase()) ||
                !summary.endsWith(".")) {
                addIssue(KEY, translate(MESSAGE), summaryNode.key());
            }
        }
    }
}