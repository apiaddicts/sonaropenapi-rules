package apiaddicts.sonar.openapi.checks.schemas;

import com.sonar.sslr.api.AstNode;

import org.json.JSONArray;
import org.json.JSONObject;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

public abstract class AbstractSchemaCheck extends BaseCheck {

    private String key;
    protected JsonNode externalRefNode= null;

    private static final String GENERIC_PROPERTY_MISSING = "generic.property-missing";
    private static final String ITEMS = "items";
    private static final String PROPERTIES = "properties";

    protected AbstractSchemaCheck(String key) {
        this.key = key;
    }

    private <T> T handleExternalRef(JsonNode node, java.util.function.Function<JsonNode, T> action) {
        boolean setExternal = false;
        if (isExternalRef(node) && externalRefNode == null) {
            externalRefNode = node;
            setExternal = true;
        }
        try {
            return action.apply(resolve(node));
        } finally {
            if (setExternal) externalRefNode = null;
        }
    }

    protected Map<String, JsonNode> getAllProperties(JsonNode schemaNode) {
        final Map<String, JsonNode> properties = new HashMap<>();

        JsonNode propertiesNode = getProperties(schemaNode);
        if (!propertiesNode.isMissing()) {
            properties.putAll(propertiesNode.propertyMap());
        }

        JsonNode allOfNode = schemaNode.get("allOf");
        if (!allOfNode.isMissing()) {
            for (JsonNode element : allOfNode.elements()) {
                handleExternalRef(element, resolved -> {
                    properties.putAll(getAllProperties(resolved));
                    return null;
                });
            }
        }
        return properties;
    }


    protected Optional<JsonNode> validateProperty(Map<String, JsonNode> properties, String propertyName, String propertyType, JsonNode parentNode) {
        if (!properties.containsKey(propertyName)) {
            addIssue(key, translate(GENERIC_PROPERTY_MISSING, propertyName), getTrueNode(parentNode));
            return Optional.empty();
        }
        JsonNode prop = properties.get(propertyName);
        return validateProperty(prop, propertyName, propertyType, parentNode);
    }

    protected Optional<JsonNode> validateProperty(JsonNode properties, String propertyName, String propertyType) {
        return handleExternalRef(properties, resolvedProps -> {
            if (resolvedProps.propertyNames().isEmpty()) {
                addIssue(key, translate(GENERIC_PROPERTY_MISSING, propertyName), getTrueNode(resolvedProps));
                return Optional.empty();
            }
            JsonNode prop = resolvedProps.get(propertyName);
            return validateProperty(prop, propertyName, propertyType, getTrueNode(resolvedProps.key()));
        });
    }

    protected Optional<JsonNode> validateProperty(JsonNode prop, String propertyName, String propertyType, JsonNode parentNode) {
        if (prop.isMissing()) {
            addIssue(key, translate(GENERIC_PROPERTY_MISSING, propertyName), getTrueNode(parentNode));
            return Optional.empty();
        }

        return handleExternalRef(prop, resolvedProp -> {
            JsonNode type = getType(resolvedProp);
            if (!isType(type, propertyType)) {
                JsonNode errorNode = (type.isMissing() ? resolvedProp : type.key());
                addIssue(key, translate("generic.property-wrong-type", propertyName, propertyType), getTrueNode(errorNode));
                return Optional.empty();
            }
            return Optional.of(resolvedProp);
        });
    }

    protected void validateRequiredProperties(JsonNode schema, Set<String> requiredValues, String requiredStr) {
        JsonNode required = getRequired(schema);
        Set<String> requiredProperties = getRequiredValues(required);
        if (!requiredProperties.containsAll(requiredValues)) {
            JsonNode errorNode = (required.isMissing() ? schema : required.key());
            addIssue(key, translate("generic.required-properties", requiredStr), getTrueNode(errorNode));
        }
    }

    protected Optional<JsonNode> validateItems(JsonNode prop, String iType) {
        return handleExternalRef(prop, resolvedProp -> {
            if (!isType(getType(resolvedProp), TYPE_ARRAY)) {
                return Optional.empty();
            }

            JsonNode itemsSchema = resolvedProp.get(ITEMS);
            return handleExternalRef(itemsSchema, resolvedItems -> {
                String propName = resolvedProp.key().getTokenValue();

                if (resolvedItems.isMissing()) {
                    addIssue(key, translate("generic.property-items-missing", propName, iType), getTrueNode(resolvedProp.key()));
                    return Optional.empty();
                }
                JsonNode itemsType = getType(resolvedItems);
                if (!isType(itemsType, iType)) {
                    JsonNode errorNode = itemsType.isMissing() ? resolvedItems : itemsType.key();
                    addIssue(key, translate("generic.property-items-wrong-type", propName, iType), getTrueNode(errorNode));
                    return Optional.empty();
                }
                return Optional.of(resolvedItems);
            });
        });
    }

    protected void validateEnumValues(JsonNode property, Set<String> expected) {
        Set<String> found = getEnumValues(property);
        if (!expected.equals(found)) {
            String propertyName = property.key().getTokenValue();
            String values = expected.stream().sorted().collect(Collectors.joining(", "));
            addIssue(key, translate("generic.enum-values", propertyName, values), getTrueNode(property.key()));
        }
    }

    private Set<String> getEnumValues(JsonNode schema) {
        return schema.get("enum").elements()
                .stream().map(AstNode::getTokenValue).collect(Collectors.toSet());
    }

    protected void validateProperties(String propertyName, JSONObject propertySchema, JsonNode propertiesNode) {
        Map<String, JsonNode> propertyMap = getAllProperties(propertiesNode);
        String schemaType = (propertySchema != null && propertySchema.has("type")) ? propertySchema.getString("type") : TYPE_ANY;
        if (schemaType == null || schemaType.trim().isEmpty() || schemaType.equals("any")) schemaType = TYPE_ANY;
        if (schemaType.equals(TYPE_ANY) && propertySchema.has(PROPERTIES)) schemaType = TYPE_OBJECT;

        if (schemaType != null && !schemaType.trim().isEmpty()) {
            if (schemaType.equals(TYPE_OBJECT)) {
                validateProperty(propertyMap, propertyName, TYPE_OBJECT, propertiesNode.key()).ifPresent(propertyNode -> validateObject(propertySchema, propertyNode));
            } else if (schemaType.equals(TYPE_ARRAY)) {
                validateProperty(propertyMap, propertyName, TYPE_ARRAY, propertiesNode.key()).ifPresent(propertyNode -> validateArray(propertySchema, propertyNode));
            } else {
                validateProperty(propertyMap, propertyName, schemaType, propertiesNode.key());
            }
        }
    }

    private void validateObject(JSONObject propertySchema, JsonNode propertyNode) {
        JSONArray schemaRequired = (propertySchema != null && propertySchema.has("required")) ? propertySchema.getJSONArray("required") : null;
        JSONObject schemaProperties = (propertySchema != null && propertySchema.has(PROPERTIES)) ? propertySchema.getJSONObject(PROPERTIES) : null;

        if (schemaProperties != null && !schemaProperties.keySet().isEmpty()) {
            List<String> sortedSchemaProperties = new ArrayList<>(schemaProperties.keySet());
            Collections.sort(sortedSchemaProperties);
            sortedSchemaProperties.forEach(childProperty -> {
                JSONObject childPropertySchema = (schemaProperties != null && schemaProperties.has(childProperty)) ? schemaProperties.getJSONObject(childProperty) : null;
                validateProperties(childProperty, childPropertySchema, propertyNode);
            });
        }

        if (schemaRequired != null && schemaRequired.length() > 0 ) {
            Set<String> requiredProperties = schemaRequired.toList().stream().map(element -> (String) element).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            validateRequiredProperties(propertyNode, requiredProperties, String.join(", ", requiredProperties));
        }
    }

    private void validateArray(JSONObject propertySchema, JsonNode propertyNode) {
        JSONObject schemaItems = (propertySchema != null && propertySchema.has(ITEMS)) ? propertySchema.getJSONObject(ITEMS) : null;
        String itemsType = (schemaItems != null && schemaItems.has("type")) ? schemaItems.getString("type") : TYPE_ANY;
        if (itemsType == null || itemsType.trim().isEmpty() || itemsType.equals("any")) itemsType = TYPE_ANY;
        if (itemsType.equals(TYPE_ANY) && schemaItems.has(PROPERTIES)) itemsType = TYPE_OBJECT;

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

    protected JsonNode getTrueNode (JsonNode node){
        return externalRefNode== null ? node : externalRefNode;
    }
}

