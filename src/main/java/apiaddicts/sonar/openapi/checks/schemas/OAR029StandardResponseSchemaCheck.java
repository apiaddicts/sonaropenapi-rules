package apiaddicts.sonar.openapi.checks.schemas;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

import apiaddicts.sonar.openapi.utils.ExternalRefHandler;

@Rule(key = OAR029StandardResponseSchemaCheck.KEY)
public class OAR029StandardResponseSchemaCheck extends AbstractSchemaCheck {

    public static final String KEY = "OAR029";

    private static final String RESPONSE_SCHEMA = "{\"type\":\"object\",\"properties\":{\"status\":{\"type\":\"object\",\"properties\":{\"code\":{\"type\":\"integer\"},\"description\":{\"type\":\"string\"},\"internal_code\":{\"type\":\"string\"},\"errors\":{\"type\":\"array\",\"nullable\":true,\"items\":{\"type\":\"object\",\"properties\":{\"name\":{\"type\":\"string\"},\"value\":{\"type\":\"string\"}}}}},\"required\":[\"code\"]},\"payload\":{\"type\":\"any\"}},\"required\":[\"status\",\"payload\"]}";

    @RuleProperty(
            key = "response-schema",
            description = "Response Schema as String",
            defaultValue = RESPONSE_SCHEMA
    )
    private String responseSchemaStr = RESPONSE_SCHEMA;

    private JSONArray requiredOnSuccess;
    private JSONArray requiredOnError;
    private JSONArray requiredAlways;
    private JSONObject responseSchemaProperties;
    private String dataProperty;
    private String rootProperty;

    private static final String DEFAULT_EXCLUSION = "/status";
    @RuleProperty(
            key = "path-exclusions",
            description = "List of explicit paths to exclude from this rule.",
            defaultValue = DEFAULT_EXCLUSION
    )
    private String exclusionStr = DEFAULT_EXCLUSION;
    private Set<String> exclusion;

    private final ExternalRefHandler handleExternalRef = new ExternalRefHandler();

    public OAR029StandardResponseSchemaCheck() {
        super(KEY);
    }

    @Override
    protected void visitFile(JsonNode root) {
        exclusion = Arrays.stream(exclusionStr.split(",")).map(String::trim).collect(Collectors.toSet());

        try {
            JSONObject responseSchema = new JSONObject(responseSchemaStr);
            requiredOnSuccess = (responseSchema.has("requiredOnSuccess")) ? responseSchema.getJSONArray("requiredOnSuccess") : null;
            requiredOnError = (responseSchema.has("requiredOnError")) ? responseSchema.getJSONArray("requiredOnError") : null;
            requiredAlways = (responseSchema.has("requiredAlways")) ? responseSchema.getJSONArray("requiredAlways") : null;
            responseSchemaProperties = (responseSchema.has("properties")) ? responseSchema.getJSONObject("properties") : null;
            dataProperty = (responseSchema.has("dataProperty")) ? responseSchema.getString("dataProperty") : null;
            rootProperty = (responseSchema.has("rootProperty")) ? responseSchema.getString("rootProperty") : null;
        } catch (JSONException err) {
        addIssue(KEY, "Error parsing Standard Response Schemas", handleExternalRef.getTrueNode(root.key()));
        }
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitPathNode(node);
    }

    private void visitPathNode(JsonNode node) {
        if (exclusion.contains(node.key().getTokenValue())) return;

        node.properties().stream()
            .filter(prop -> isOperation(prop))
            .map(JsonNode::value)
            .map(operation -> operation.get("responses"))
            .filter(responses -> !responses.isMissing())
            .flatMap(responses -> responses.properties().stream())
            .forEach(propResponse -> {
                String statusCode = propResponse.key().getTokenValue();
                handleExternalRef.resolve(propResponse.value(), resolved -> {
                    if (resolved.getType().equals(OpenApi2Grammar.RESPONSE)) {
                        visitSchemaNode(resolved, statusCode);
                    } else if (resolved.getType().equals(OpenApi3Grammar.RESPONSE)) {
                        resolved.at("/content").propertyMap().forEach((mediaType, mediaTypeNode) -> {
                            if (mediaType.toLowerCase().contains("json")) {
                                visitSchemaNode(mediaTypeNode, statusCode);
                            }
                        });
                    }
                });
            });
    }

    private void visitSchemaNode(JsonNode responseNode, String statusCode) {
        boolean successCode = false;
        int code = 0;
        if (!statusCode.equalsIgnoreCase("default")) {
            code = Integer.parseInt(statusCode);
            successCode = 200 <= code && 300 > code && code != 204;
        }

        if (code == 204) return;

        JsonNode schemaNode = responseNode.value().get("schema");
        if (schemaNode.isMissing()) return;

        schemaNode = handleExternalRef.resolve(schemaNode, resolved -> resolved);
        Map<String, JsonNode> properties = getAllProperties(schemaNode);

        if (rootProperty != null) {
            if (rootProperty.equals("*")) {
                rootProperty = properties.entrySet().iterator().next().getKey();
            }

            validateProperty(properties, rootProperty, "object", schemaNode.key()).ifPresent(node -> {
                Map<String, JsonNode> allProp = getAllProperties(node);
                if (allProp.isEmpty()) {
                    addIssue(KEY, translate("OAR029.error-required-one-property", rootProperty), handleExternalRef.getTrueNode(node.key()));
                }
            });

            schemaNode = properties.get(rootProperty);
            properties = getAllProperties(properties.get(rootProperty));
        }

        if (successCode) {
            validateRootProperties(requiredOnSuccess, properties, schemaNode);
        } else {
            validateRootProperties(requiredOnError, properties, schemaNode);
        }
        validateRootProperties(requiredAlways, properties, schemaNode);
    }

    private void validateRootProperties(JSONArray requiredPropsArray, Map<String, JsonNode> properties, JsonNode parentNode) {
        if (requiredPropsArray == null || requiredPropsArray.isEmpty()) return;

        requiredPropsArray.toList().stream()
            .map(String::valueOf)
            .forEach(name -> {
                JSONObject propSchema = (responseSchemaProperties != null && responseSchemaProperties.has(name)) 
                    ? responseSchemaProperties.getJSONObject(name) : null;

                if (name.equals(dataProperty)) {
                    validateDataProperty(name, propSchema, properties, parentNode);
                } else {
                    validateProperties(name, propSchema, parentNode);
                }
            });
    }

    private void validateDataProperty(String name, JSONObject schema, Map<String, JsonNode> properties, JsonNode parent) {
        String type = (schema != null && schema.has("type")) ? schema.getString("type") : TYPE_ANY;
        if ("any".equals(type)) type = TYPE_ANY;

        validateProperty(properties, name, type, parent.key()).ifPresent(node -> {
            boolean isArray = "array".equals(parent.get("type").getTokenValue());
            if (getAllProperties(node).isEmpty() && !isArray) {
                addIssue(KEY, translate("OAR029.error-required-one-property", name), handleExternalRef.getTrueNode(node.key()));
            }
        });
    }
}