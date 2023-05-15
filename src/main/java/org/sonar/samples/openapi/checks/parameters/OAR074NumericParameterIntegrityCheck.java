package org.sonar.samples.openapi.checks.parameters;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.samples.openapi.checks.BaseCheck;

import java.util.Set;

@Rule(key = OAR074NumericParameterIntegrityCheck.KEY)
public class OAR074NumericParameterIntegrityCheck extends BaseCheck {

    public static final String KEY = "OAR074";
    private static final String MESSAGE = "OAR074.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() instanceof OpenApi2Grammar) {
            visitSwaggerParameterNode(node);
        } else {
            visitParameterNode(node);
        }
    }

    public void visitParameterNode(JsonNode node) {
        JsonNode schemaNode = node.get("schema");

        if (schemaNode != null) {
            JsonNode typeNode = schemaNode.get("type");

            boolean isNumericType = typeNode != null && ("integer".equals(typeNode.getTokenValue()) ||
                "number".equals(typeNode.getTokenValue()) ||
                "float".equals(typeNode.getTokenValue()));

            if (isNumericType) {
                JsonNode minNode = schemaNode.get("minimum");
                JsonNode maxNode = schemaNode.get("maximum");
                JsonNode formatNode = schemaNode.get("format");

                boolean lacksRestriction = (minNode.isMissing() && maxNode.isMissing() && formatNode.isMissing());
                if (lacksRestriction) {
                    addIssue(KEY, translate(MESSAGE), typeNode);
                }
            }
        }
    }

    public void visitSwaggerParameterNode(JsonNode node) {
        JsonNode typeNode = node.get("type");

        boolean isNumericType = typeNode != null && ("integer".equals(typeNode.getTokenValue()) ||
            "number".equals(typeNode.getTokenValue()) ||
            "float".equals(typeNode.getTokenValue()));

        if (isNumericType) {
            JsonNode minNode = node.get("minimum");
            JsonNode maxNode = node.get("maximum");
            JsonNode formatNode = node.get("format");

            boolean lacksRestriction = (minNode.isMissing() && maxNode.isMissing() && formatNode.isMissing());
            if (lacksRestriction) {
                addIssue(KEY, translate(MESSAGE), typeNode);
            }
        }
    }
}