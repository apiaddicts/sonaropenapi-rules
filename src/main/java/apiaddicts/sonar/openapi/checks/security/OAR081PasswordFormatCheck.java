package apiaddicts.sonar.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import java.util.Set;

@Rule(key = OAR081PasswordFormatCheck.KEY)
public class OAR081PasswordFormatCheck extends BaseCheck {

    public static final String KEY = "OAR081";
    private static final String MESSAGE = "OAR081.error";

    private static final String PROPERTIES = "properties";
    private static final String SCHEMA = "schema";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi2Grammar.DEFINITIONS, OpenApi3Grammar.PATHS, OpenApi3Grammar.COMPONENTS, OpenApi31Grammar.PATHS, OpenApi31Grammar.COMPONENTS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType()) || OpenApi31Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        } else if (OpenApi2Grammar.DEFINITIONS.equals(node.getType())) {
            visitDefinitionsNode(node);
        } else if (OpenApi3Grammar.COMPONENTS.equals(node.getType())) {
            JsonNode schemasNode = node.get("schemas");
            if (!schemasNode.isMissing()) {
                visitDefinitionsNode(schemasNode);
            }
        } else if (OpenApi31Grammar.COMPONENTS.equals(node.getType())) {
            JsonNode schemasNode = node.get("schemas");
            if (!schemasNode.isMissing()) {
                visitDefinitionsNode(schemasNode);
            }
        }
    }

    private void visitDefinitionsNode(JsonNode definitionsNode) {
        for (JsonNode schemaNode : definitionsNode.propertyMap().values()) {
            JsonNode propertiesNode = schemaNode.get(PROPERTIES);
            if (!propertiesNode.isMissing()) {
                validatePasswordFormat(propertiesNode);
            }
        }
    }

    private void visitPathsNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                validateOperationNode(operationNode);
            }
        }
    }

    private void validateOperationNode(JsonNode operationNode) {
        JsonNode parametersNode = operationNode.get("parameters");
        if (!parametersNode.isMissing()) {
            for (JsonNode parameterNode : parametersNode.elements()) {
                if ("body".equals(parameterNode.get("in").getTokenValue())) {
                    validateRequestBody(parameterNode);
                }
            }
        }
        else {
            validateRequestBody(operationNode.get("requestBody"));
        }
        validateResponses(operationNode.get("responses"));
    }

    private void validateRequestBody(JsonNode requestBodyNode) {
        if (requestBodyNode.isMissing()) return;

        JsonNode schemaNode = requestBodyNode.get(SCHEMA);
        if (!schemaNode.isMissing()) {
            validatePasswordFormat(schemaNode.get(PROPERTIES));
        } else {
            validateContentNode(requestBodyNode.get("content"));
        }
    }

    private void validateResponses(JsonNode responsesNode) {
        if (responsesNode.isMissing()) return;

        for (JsonNode responseNode : responsesNode.propertyMap().values()) {
            JsonNode schemaNode = responseNode.get(SCHEMA);
            if (!schemaNode.isMissing()) {
                validatePasswordFormat(schemaNode.get(PROPERTIES));
            } else {
                validateContentNode(responseNode.get("content"));
            }
        }
    }

    private void validateContentNode(JsonNode contentNode) {
        if (contentNode.isMissing()) return;

        for (JsonNode mediaTypeNode : contentNode.propertyMap().values()) {
            JsonNode schemaNode = mediaTypeNode.get(SCHEMA);
            if (!schemaNode.isMissing()) {
                validatePasswordFormat(schemaNode.get(PROPERTIES));
            }
        }
    }

    private void validatePasswordFormat(JsonNode propertiesNode) {
        if (!propertiesNode.isMissing()) {
            JsonNode passwordNode = propertiesNode.get("password");
            if (!passwordNode.isMissing() && !"password".equals(passwordNode.get("format").getTokenValue())) {
                addIssue(KEY, translate(MESSAGE), passwordNode.key());
            }
        }
    }
}