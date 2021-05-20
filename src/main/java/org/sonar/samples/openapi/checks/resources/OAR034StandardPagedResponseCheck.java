package org.sonar.samples.openapi.checks.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR034StandardPagedResponseCheck.KEY)
public class OAR034StandardPagedResponseCheck extends AbstractExplicitResponseCheck {

    public static final String KEY = "OAR034";
    private static final String PAGING_SCHEMA = "{\"type\":\"object\",\"properties\":{\"numPages\":{\"type\":\"integer\"},\"total\":{\"type\":\"integer\"},\"start\":{\"type\":\"integer\"},\"limit\":{\"type\":\"integer\"},\"links\":{\"type\":\"object\",\"properties\":{\"next\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"previous\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"last\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"self\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}},\"first\":{\"type\":\"object\",\"properties\":{\"href\":{\"type\":\"string\"}}}},\"required\":[\"self\",\"previous\",\"next\"]}},\"required\":[\"start\",\"limit\",\"links\"],\"pagingPropertyName\":\"paging\"}";

    @RuleProperty(
            key = "paging-schema",
            description = "Response Schema as String",
            defaultValue = PAGING_SCHEMA
    )
    private String pagingSchemaStr = PAGING_SCHEMA;
    private JSONObject pagingSchema;

    private String pagingPropertyName = null;

    public OAR034StandardPagedResponseCheck() {
        super(KEY, "206");
    }

    @Override
    protected void visitFile(JsonNode root) {
        try {
            pagingSchema = new JSONObject(pagingSchemaStr);
            pagingPropertyName = (pagingSchema.has("pagingPropertyName")) ? pagingSchema.getString("pagingPropertyName") : null;
        } catch (JSONException err) {
			addIssue(KEY, "Error parsing Standard Response Schemas", root.key());
        }
    }

    @Override
    protected void visitV2ExplicitNode(JsonNode node) {
        JsonNode schemaNode = node.get("schema");
        if (schemaNode.isMissing()) return;
        schemaNode = resolve(schemaNode);
        validateProperties(pagingPropertyName, pagingSchema, schemaNode);
    }

    private void validateProperties(String propertyName, JSONObject propertySchema, JsonNode propertiesNode) {
        Map<String, JsonNode> propertyMap = getAllProperties(propertiesNode);
        String schemaType = (propertySchema != null && propertySchema.has("type")) ? propertySchema.getString("type") : TYPE_ANY;
        if ((schemaType == null || schemaType.equals(TYPE_ANY) || schemaType.isBlank()) && propertySchema.has("properties")) schemaType = TYPE_OBJECT;

        if (schemaType != null && !schemaType.isBlank()) {
            if (schemaType.equals(TYPE_OBJECT)) {
                validateProperty(propertyMap, propertyName, TYPE_OBJECT, propertiesNode.key()).ifPresent(propertyNode -> {                    
                    validateObject(propertySchema, propertyNode);
                });
            } else if (schemaType.equals(TYPE_ARRAY)) {
                validateProperty(propertyMap, propertyName, TYPE_ARRAY, propertiesNode.key()).ifPresent(propertyNode -> {
                    validateArray(propertySchema, propertyNode);
                });
            } else {
                validateProperty(propertyMap, propertyName, schemaType, propertiesNode.key());
            }
        }
    }

    private void validateObject(JSONObject propertySchema, JsonNode propertyNode) {
        JSONArray schemaRequired = (propertySchema != null && propertySchema.has("required")) ? propertySchema.getJSONArray("required") : null;
        JSONObject schemaProperties = (propertySchema != null && propertySchema.has("properties")) ? propertySchema.getJSONObject("properties") : null;

        Map<String, JsonNode> propProperties = getAllProperties(propertyNode);
        if (propProperties == null || propProperties.isEmpty()) return;

        if (schemaRequired != null && schemaRequired.length() > 0 ) {
            Set<String> requiredProperties = schemaRequired.toList().stream().map(element -> (String) element).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            validateRequiredProperties(propertyNode, requiredProperties, String.join(", ", requiredProperties));
        }

        if (schemaProperties != null && !schemaProperties.keySet().isEmpty()) {
            List<String> sortedSchemaProperties = new ArrayList<>(schemaProperties.keySet());
            Collections.sort(sortedSchemaProperties);
            sortedSchemaProperties.forEach(childProperty -> {
                JSONObject childPropertySchema = (schemaProperties != null && schemaProperties.has(childProperty)) ? schemaProperties.getJSONObject(childProperty) : null;
                validateProperties(childProperty, childPropertySchema, propertyNode);
            });
        }
    }

    private void validateArray(JSONObject propertySchema, JsonNode propertyNode) {
        JSONObject schemaItems = (propertySchema != null && propertySchema.has("items")) ? propertySchema.getJSONObject("items") : null;
        String itemsType = (schemaItems != null && schemaItems.has("type")) ? schemaItems.getString("type") : TYPE_ANY;    
        if ((itemsType == null || itemsType.equals(TYPE_ANY) || itemsType.isBlank()) && schemaItems.has("properties")) itemsType = TYPE_OBJECT;
        if (itemsType.equals(TYPE_OBJECT)) {
            validateItems(propertyNode, itemsType).ifPresent(itemNode -> {
                validateObject(schemaItems, itemNode);
            });
        } else if (itemsType.equals(TYPE_ARRAY)) {
            validateItems(propertyNode, itemsType).ifPresent(itemNode -> {
                validateArray(schemaItems, itemNode);
            });
        } else {
            validateItems(propertyNode, itemsType);
        }
    }
}
