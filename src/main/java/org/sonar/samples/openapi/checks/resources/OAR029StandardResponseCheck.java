package org.sonar.samples.openapi.checks.resources;

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

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR029StandardResponseCheck.KEY)
public class OAR029StandardResponseCheck extends AbstractSchemaCheck {

    public static final String KEY = "OAR029";

    //MD
    //private static final String RESPONSE_SCHEMA = "{\"type\":\"object\",\"properties\":{\"result\":{\"type\":\"object\",\"properties\":{\"http_code\":{\"type\":\"integer\"},\"status\":{\"type\":\"boolean\"},\"trace_id\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"name\",\"value\"]}}},\"required\":[\"status\",\"http_code\",\"trace_id\"]},\"data\":{\"type\":\"any\"}},\"requiredOnError\":[],\"requiredOnSuccess\":[\"data\"],\"requiredAlways\":[\"result\"],\"dataProperty\":\"data\"}";
    //RIM
    //private static final String RESPONSE_SCHEMA = "{\"type\":\"object\",\"properties\":{\"error\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"string\"},\"message\":{\"type\":\"string\"},\"httpStatus\":{\"type\":\"integer\"},\"details\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}}},\"required\":[\"code\",\"message\",\"httpStatus\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnError\":[\"error\"],\"requiredOnSuccess\":[\"payload\"],\"dataProperty\":\"payload\"}";
    //Cloudappi
    private static final String RESPONSE_SCHEMA = "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"http_status\":{\"type\":\"string\"},\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"http_status\",\"code\",\"description\",\"errors\"]},\"payload\":{\"type\":\"any\"}},\"requiredOnSuccess\":[\"data\"],\"requiredAlways\":[\"status\"],\"dataProperty\":\"data\"}";

    @RuleProperty(
            key = "response-schema",
            description = "Response Schema as String",
            defaultValue = RESPONSE_SCHEMA
    )
    private String responseSchemaStr = RESPONSE_SCHEMA;
    private JSONObject responseSchema;

    private JSONArray requiredOnSuccess = null;
    private JSONArray requiredOnError = null;
    private JSONArray requiredAlways = null;
    private JSONObject responseSchemaProperties = null;
    private String dataProperty = null;

    private static final String DEFAULT_EXCLUSION = "/status";
    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;
    private Set<String> exclusion;

    public OAR029StandardResponseCheck() {
        super(KEY);
    }

    @Override
    protected void visitFile(JsonNode root) {
        exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());

        try {
            responseSchema = new JSONObject(responseSchemaStr);
            requiredOnSuccess = (responseSchema.has("requiredOnSuccess")) ? responseSchema.getJSONArray("requiredOnSuccess") : null;
            requiredOnError = (responseSchema.has("requiredOnError")) ? responseSchema.getJSONArray("requiredOnError") : null;
            requiredAlways = (responseSchema.has("requiredAlways")) ? responseSchema.getJSONArray("requiredAlways") : null;
            responseSchemaProperties = (responseSchema.has("properties")) ? responseSchema.getJSONObject("properties") : null;
            dataProperty = (responseSchema.has("dataProperty")) ? responseSchema.getString("dataProperty") : null;
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
        List<JsonNode> allResponses = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)) // operations
                .map(JsonNode::value)
                .flatMap(n -> n.properties().stream()) // responses
                .map(JsonNode::value)
                .flatMap(n -> n.properties().stream()) // response
                .collect(Collectors.toList());
        for (JsonNode responseNode : allResponses) {
            String statusCode = responseNode.key().getTokenValue();
            responseNode = resolve(responseNode);

            if (responseNode.getType().equals(OpenApi2Grammar.RESPONSE)) {
                visitSchemaNode(responseNode, statusCode);
            } else if (responseNode.getType().equals(OpenApi3Grammar.RESPONSE)) {
                JsonNode content = responseNode.at("/content");
                if (content.isMissing()) continue;

                content.propertyMap().forEach((mediaType, mediaTypeNode) -> {
                    if (!mediaType.toLowerCase().contains("json")) return;
                    visitSchemaNode(mediaTypeNode, statusCode);
                });
            }
        }
    }

    private void visitSchemaNode(JsonNode responseNode, String statusCode) {
        boolean successCode = false;
        int code = 0;
        if (!statusCode.equalsIgnoreCase("default")) {
            code = Integer.parseInt(statusCode);
            successCode = 200 <= code && 300 > code && code != 204;
        }

        JsonNode schemaNode = responseNode.value().get("schema");
        if (schemaNode.isMissing()) return;
        schemaNode = resolve(schemaNode);

        Map<String, JsonNode> properties = getAllProperties(schemaNode);

        if (successCode) {
            validateRootProperties(requiredOnSuccess, properties, schemaNode);
        } else {
            validateRootProperties(requiredOnError, properties, schemaNode);
        }

        if (code != 204) {
            validateRootProperties(requiredAlways, properties, schemaNode);
        }
    }

    private void validateRootProperties(JSONArray requiredPropertiesJSONArray, Map<String, JsonNode> properties, JsonNode parentNode) {
        if (requiredPropertiesJSONArray != null && requiredPropertiesJSONArray.length() > 0) {
            Set<String> requiredProperties = requiredPropertiesJSONArray.toList().stream().map(element -> (String) element).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            requiredProperties.forEach(propertyName -> {
                JSONObject propertySchema = (responseSchemaProperties != null && responseSchemaProperties.has(propertyName)) ? responseSchemaProperties.getJSONObject(propertyName) : null;
                if (propertyName.equals(dataProperty)) {
                    String propertyType = (propertySchema != null && propertySchema.has("type")) ? propertySchema.getString("type") : TYPE_ANY;
                    if (propertyType == null || propertyType.trim().isEmpty() || propertyType.equals("any")) propertyType = TYPE_ANY;
                    validateProperty(properties, propertyName, propertyType, parentNode.key()).ifPresent(node -> {
                        Map<String, JsonNode> allProp = getAllProperties(parentNode);
                        if (allProp.isEmpty() && !parentNode.get("type").getTokenValue().equals("array")) {
                            addIssue(KEY, translate("OAR029.error-required-one-property", propertyName), parentNode.key());
                        }
                    });
                } else {
                    validateProperties(propertyName, propertySchema, parentNode);
                }
            });
        }
    }
}
