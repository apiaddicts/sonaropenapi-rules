package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

@Rule(key = OAR072NonOKModelResponseCheck.KEY)
public class OAR072NonOKModelResponseCheck extends BaseCheck {

    public static final String KEY = "OAR072";
    private static final String MESSAGE = "OAR072.error";

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
            JsonNode jsonNode = responsesNode.get("400");
            if (jsonNode != null) {
                if (node.is(OpenApi2Grammar.OPERATION)) {
                    checkSwaggerResponse(jsonNode);
                } else if (node.is(OpenApi3Grammar.OPERATION)) {
                    checkOpenApiResponse(jsonNode);
                }
            }
        }
    }

    private void checkSwaggerResponse(JsonNode jsonNode) {
        JsonNode schemaNode = jsonNode.get("schema");
        if (schemaNode != null) {
            JsonNode propertiesNode = schemaNode.get("properties");
            if (propertiesNode != null) {
                JsonNode stackTraceNode = propertiesNode.get("stackTrace");
                if (stackTraceNode.isMissing()) {
                    return;
                }else{
                    addIssue(KEY, translate(MESSAGE), stackTraceNode.key());
                }
            }
        }
    }

    private void checkOpenApiResponse(JsonNode jsonNode) {
        JsonNode contentNode = jsonNode.get("content");
        if (contentNode != null) {
            JsonNode mediaTypeNode = contentNode.get("application/json");
            if (mediaTypeNode != null) {
                JsonNode schemaNode = mediaTypeNode.get("schema");
                if (schemaNode != null) {
                    JsonNode propertiesNode = schemaNode.get("properties");
                    if (propertiesNode != null) {
                        JsonNode stackTraceNode = propertiesNode.get("stackTrace");
                        if (stackTraceNode.isMissing()) {
                            return;
                        }else{
                            addIssue(KEY, translate(MESSAGE), stackTraceNode.key());
                        }
                            
                        
                    }
                }
            }
        }
    }
}