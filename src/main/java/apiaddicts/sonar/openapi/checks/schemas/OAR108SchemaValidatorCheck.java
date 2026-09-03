package apiaddicts.sonar.openapi.checks.schemas;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import com.sonar.sslr.api.TokenType;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.snakeyaml.parser.Tokens;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import apiaddicts.sonar.openapi.utils.JsonNodeUtils;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Rule(key = OAR108SchemaValidatorCheck.KEY)
public class OAR108SchemaValidatorCheck extends BaseCheck {

    public static final String KEY = "OAR108";
    private static final String MESSAGE = "OAR108.error";
    private static final String TYPE_NULL = "null";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi31Grammar.PATHS, OpenApi32Grammar.PATHS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (!node.isObject()) return;

        for (JsonNode pathNode : node.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                processResponses(operationNode.get("responses"));
            }
        }
    }

    private void processResponses(JsonNode responses) {
        if (responses == null || !responses.isObject()) return;

        for (JsonNode res : responses.propertyMap().values()) {
            JsonNode content = res.get("content");

            if (content != null && !content.isMissing() && !content.isNull() && content.isObject()) {
                for (JsonNode mediaType : content.propertyMap().values()) {
                    validateTypes(mediaType.get("schema"), mediaType.get("example"), false);
                }
            } else {
                validateTypes(res.get("schema"), res.get("examples"), true);
            }
        }
    }

    private void validateTypes(JsonNode schema, JsonNode example, boolean isSwagger) {
        if (schema == null || schema.isMissing() || schema.isNull() || 
            example == null || example.isMissing() || example.isNull()) return;

        Map<String, String> schemaTypes = extractSchemaTypes(schema);
        Map<String, String> exampleTypes = isSwagger ? extractExampleTypesSwagger2(example) : extractExampleTypes(example);

        schemaTypes.forEach((keyName, expectedType) -> {
            String actualType = exampleTypes.getOrDefault(keyName, "unknown");
            if (!isCompatible(expectedType, actualType)) {
                addIssue(KEY, translate(MESSAGE), example.key());
            }
        });
    }

    private boolean isCompatible(String expectedType, String actualType) {
        if (expectedType == null) return true;
        if (TYPE_NULL.equals(actualType)) return true;
        if (expectedType.equals(actualType)) return true;
        return "number".equals(expectedType) && "integer".equals(actualType);
    }

    private Map<String, String> extractSchemaTypes(JsonNode schemaNode) {
        Map<String, String> schemaTypes = new HashMap<>();

        JsonNode propertiesNode = schemaNode.get("properties");
        if (propertiesNode != null && propertiesNode.isObject()) {
            for (Map.Entry<String, JsonNode> entry : propertiesNode.propertyMap().entrySet()) {
                String propertyName = entry.getKey();
                JsonNode propertyTypeNode = entry.getValue().get("type");
                String propertyType = JsonNodeUtils.getPrimaryType(propertyTypeNode);
                schemaTypes.put(propertyName, propertyType);
            }
        }

        return schemaTypes;
    }

    private Map<String, String> extractExampleTypes(JsonNode exampleNode) {
        Map<String, String> exampleTypes = new HashMap<>();

        if (exampleNode != null && exampleNode.isObject()) {
            for (Map.Entry<String, JsonNode> entry : exampleNode.propertyMap().entrySet()) {
                String propertyName = entry.getKey();
                JsonNode propertyValueNode = entry.getValue();
                String propertyType = determineExampleType(propertyValueNode);
                exampleTypes.put(propertyName, propertyType);
            }
        }

        return exampleTypes;
    }

    private Map<String, String> extractExampleTypesSwagger2(JsonNode examplesNode) {
        Map<String, String> exampleTypes = new HashMap<>();

        for (JsonNode exampleMediaTypeNode : examplesNode.propertyMap().values()) {
            if (exampleMediaTypeNode.isObject()) {
                for (Map.Entry<String, JsonNode> entry : exampleMediaTypeNode.propertyMap().entrySet()) {
                    String propertyName = entry.getKey();
                    JsonNode propertyValueNode = entry.getValue();
                    String propertyType = determineExampleType(propertyValueNode);
                    exampleTypes.put(propertyName, propertyType);
                }
            }
        }

        return exampleTypes;
    }

    private String determineExampleType(JsonNode node) {
        if (node.isObject()) {
            return "object";
        }
        if (node.isArray()) {
            return "array";
        }
        if (node.isNull()) {
            return TYPE_NULL;
        }

        TokenType tokenType = node.getToken().getType();
        if (tokenType == Tokens.INTEGER) {
            return "integer";
        }
        if (tokenType == Tokens.FLOAT) {
            return "number";
        }
        if (tokenType == Tokens.TRUE || tokenType == Tokens.FALSE) {
            return "boolean";
        }
        if (tokenType == Tokens.NULL) {
            return TYPE_NULL;
        }

        return "string";
    }
}