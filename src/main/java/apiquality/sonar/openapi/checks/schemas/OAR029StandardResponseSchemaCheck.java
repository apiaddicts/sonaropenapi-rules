package apiquality.sonar.openapi.checks.schemas;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;
    
    @Rule(key = OAR029StandardResponseSchemaCheck.KEY)
    public class OAR029StandardResponseSchemaCheck extends AbstractSchemaCheck {
    
        public static final String KEY = "OAR029";
        private static final String DEFAULT_RESPONSE_SCHEMA = "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"code\"]},\"payload\":{\"type\":\"any\"}},\"required\":[\"status\",\"payload\"]}";
    
        @RuleProperty(
                key = "response-schema",
                description = "Response Schema as String",
                defaultValue = DEFAULT_RESPONSE_SCHEMA
        )
        private String responseSchemaStr = DEFAULT_RESPONSE_SCHEMA;
        private JSONObject responseSchema;
    
        private JSONArray requiredOnSuccess = null;
        private JSONArray requiredAlways = null;
        private JSONObject responseSchemaProperties = null;
        private String dataProperty = null;
        private String rootProperty = null;
    
        private static final String DEFAULT_EXCLUSION = "/status";
        @RuleProperty(
                key = "path-exclusions",
                description = "List of explicit paths to exclude from this rule.",
                defaultValue = DEFAULT_EXCLUSION
        )
        private String exclusionStr = DEFAULT_EXCLUSION;
        private Set<String> exclusion;
    
        public OAR029StandardResponseSchemaCheck() {
            super(KEY);
        }
    
        @Override
        protected void visitFile(JsonNode root) {
            exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());
            try {
                responseSchema = new JSONObject(responseSchemaStr);
                requiredOnSuccess = responseSchema.optJSONArray("requiredOnSuccess");
                requiredAlways = responseSchema.optJSONArray("requiredAlways");
                responseSchemaProperties = responseSchema.optJSONObject("properties");
                dataProperty = responseSchema.optString("dataProperty", null);
                rootProperty = responseSchema.optString("rootProperty", null);
            } catch (JSONException err) {
                addIssue(KEY, "Error parsing Standard Response Schemas", root.key());
            }
        }
    
        @Override
        public Set<AstNodeType> subscribedKinds() {
            return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
        }
    
        @Override
        public void visitNode(JsonNode node) {
            visitPathNode(node);
        }
    
        private void visitPathNode(JsonNode node) {
            String path = node.key().getTokenValue();
            if (exclusion.contains(path)) return;
            List<JsonNode> allResponses = node.properties().stream()
                    .filter(propertyNode -> isOperation(propertyNode))
                    .map(JsonNode::value)
                    .flatMap(n -> n.properties().stream())
                    .map(JsonNode::value)
                    .flatMap(n -> n.properties().stream())
                    .collect(Collectors.toList());
    
            for (JsonNode responseNode : allResponses) {
                String statusCode = responseNode.key().getTokenValue();
                responseNode = resolve(responseNode);
    
                JsonNode content = responseNode.at("/content");
                if (content.isMissing()) continue;
    
                content.propertyMap().forEach((mediaType, mediaTypeNode) -> {
                    if (!mediaType.toLowerCase().contains("json")) return;
                    visitSchemaNode(mediaTypeNode, statusCode);
                });
            }
        }
    
        private void visitSchemaNode(JsonNode mediaTypeNode, String statusCode) {
            JsonNode schemaNode = mediaTypeNode.value().get("schema");
            if (schemaNode.isMissing()) return;
    
            schemaNode = resolve(schemaNode);
            Map<String, JsonNode> properties = getAllProperties(schemaNode);
    
            if (rootProperty != null && properties.containsKey(rootProperty)) {
                validateProperty(properties, rootProperty, "object", schemaNode.key()).ifPresent(node -> {
                    Map<String, JsonNode> allProp = getAllProperties(node);
                    if (allProp.isEmpty()) {
                        addIssue(KEY, "OAR029.error-required-one-property", node.key());
                    }
                });
    
                schemaNode = properties.get(rootProperty);
                properties = getAllProperties(schemaNode);
            }
    
            if (statusCode.startsWith("2")) {
                validateRootProperties(requiredOnSuccess, properties, schemaNode);
            }
            validateRootProperties(requiredAlways, properties, schemaNode);
        }
    
        private void validateRootProperties(JSONArray requiredPropertiesJSONArray, Map<String, JsonNode> properties, JsonNode parentNode) {
            if (requiredPropertiesJSONArray != null) {
                Set<String> requiredProperties = requiredPropertiesJSONArray.toList().stream()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.toCollection(LinkedHashSet::new));
    
                for (String propertyName : requiredProperties) {
                    JSONObject propertySchema = responseSchemaProperties.optJSONObject(propertyName);
                    validateProperties(propertyName, propertySchema, parentNode);
                }
            }
        }
    }