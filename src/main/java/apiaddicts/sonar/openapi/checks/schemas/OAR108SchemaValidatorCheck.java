package apiaddicts.sonar.openapi.checks.schemas;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Rule(key = OAR108SchemaValidatorCheck.KEY)
public class OAR108SchemaValidatorCheck extends BaseCheck {

    public static final String KEY = "OAR108";
    private static final String MESSAGE = "OAR108.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS);
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
            if (!expectedType.equals(actualType)) {
                addIssue(KEY, translate(MESSAGE), example.key());
            }
        });
    }

    private Map<String, String> extractSchemaTypes(JsonNode schemaNode) {
        Map<String, String> schemaTypes = new HashMap<>();

        JsonNode propertiesNode = schemaNode.get("properties");
        if (propertiesNode != null && propertiesNode.isObject()) {
            for (Map.Entry<String, JsonNode> entry : propertiesNode.propertyMap().entrySet()) {
                String propertyName = entry.getKey();
                JsonNode propertyTypeNode = entry.getValue().get("type");
                String propertyType = propertyTypeNode != null ? propertyTypeNode.stringValue() : null;
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
        String value = node.stringValue().trim();

        if (value.matches("-?\\d+\\.\\d+")) {
            return "number";
        } else if (value.matches("-?\\d+")) {
            return "integer";
        }

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return "boolean";
        }

        if (node.isObject()) {
            return "object";
        } else if (node.isArray()) {
            return "array";
        }

        return "string";
    }
}