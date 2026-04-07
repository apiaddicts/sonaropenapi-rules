package apiaddicts.sonar.openapi.checks.schemas;

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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

import apiaddicts.sonar.openapi.utils.ExternalRefHandler;

public abstract class AbstractSchemaCheck extends BaseCheck {

    private String key;
    private final ExternalRefHandler handleExternalRef = new ExternalRefHandler();

    private static final String GENERIC_PROPERTY_MISSING = "generic.property-missing";
    private static final String ITEMS = "items";
    private static final String PROPERTIES = "properties";

    protected AbstractSchemaCheck(String key) {
        this.key = key;
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
                handleExternalRef.resolve(element, (Consumer<JsonNode>) resolved -> properties.putAll(getAllProperties(resolved)));
            }
        }
        return properties;
    }

    protected Optional<JsonNode> validateProperty(Map<String, JsonNode> properties, String propertyName, String propertyType, JsonNode parentNode) {
        if (!properties.containsKey(propertyName)) {
            addIssue(key, translate(GENERIC_PROPERTY_MISSING, propertyName), handleExternalRef.getTrueNode(parentNode));
            return Optional.empty();
        }
        JsonNode prop = properties.get(propertyName);
        return validateProperty(prop, propertyName, propertyType, parentNode);
    }

    protected Optional<JsonNode> validateProperty(JsonNode prop, String propertyName, String propertyType, JsonNode parentNode) {
        if (prop.isMissing()) {
            addIssue(key, translate(GENERIC_PROPERTY_MISSING, propertyName), handleExternalRef.getTrueNode(parentNode));
            return Optional.empty();
        }

        return handleExternalRef.resolve(prop, resolvedProp -> {
            JsonNode type = getType(resolvedProp);
            if (isType(type, propertyType)) return Optional.of(resolvedProp);
            if (matchesTypeViaAllOf(resolvedProp, propertyType)) return Optional.of(resolvedProp);

            JsonNode errorNode = type.isMissing() ? resolvedProp : type.key();
            addIssue(key, translate("generic.property-wrong-type", propertyName, propertyType), handleExternalRef.getTrueNode(errorNode));

            return Optional.empty();
        });
    }

    protected void validateRequiredProperties(JsonNode schema, Set<String> requiredValues, String requiredStr) {
        JsonNode required = getRequired(schema);
        Set<String> requiredProperties = getRequiredValues(required);
        if (!requiredProperties.containsAll(requiredValues)) {
            JsonNode errorNode = (required.isMissing() ? schema : required.key());
            addIssue(key, translate("generic.required-properties", requiredStr), handleExternalRef.getTrueNode(errorNode));
        }
    }

    protected Optional<JsonNode> validateItems(JsonNode prop, String iType) {
        return handleExternalRef.resolve(prop, resolvedProp -> {
            if (!isType(getType(resolvedProp), TYPE_ARRAY)) {
                return Optional.empty();
            }

            JsonNode itemsSchema = resolvedProp.get(ITEMS);
            return handleExternalRef.resolve(itemsSchema, resolvedItems -> {
                String propName = resolvedProp.key().getTokenValue();

                if (resolvedItems.isMissing()) {
                    addIssue(key, translate("generic.property-items-missing", propName, iType), handleExternalRef.getTrueNode(resolvedProp.key()));
                    return Optional.empty();
                }
                JsonNode itemsType = getType(resolvedItems);
                if (!isType(itemsType, iType)) {
                    JsonNode errorNode = itemsType.isMissing() ? resolvedItems : itemsType.key();
                    addIssue(key, translate("generic.property-items-wrong-type", propName, iType), handleExternalRef.getTrueNode(errorNode));
                    return Optional.empty();
                }
                return Optional.of(resolvedItems);
            });
        });
    }

    private boolean matchesTypeViaAllOf(JsonNode node, String expectedType) {
        JsonNode allOf = node.get("allOf");
        if (allOf.isMissing()) return false;

        for (JsonNode element : allOf.elements()) {
            Boolean match = handleExternalRef.resolve(element, resolved -> {
                JsonNode resolvedType = getType(resolved);
                return isType(resolvedType, expectedType);
            });

            if (Boolean.TRUE.equals(match)) return true;
        }
        return false;
    }

    protected void validateProperties(String propertyName, JSONObject propertySchema, JsonNode propertiesNode) {
        Map<String, JsonNode> propertyMap = getAllProperties(propertiesNode);
        String schemaType = (propertySchema != null && propertySchema.has("type")) ? propertySchema.getString("type") : TYPE_ANY;
        if (schemaType == null || schemaType.trim().isEmpty() || schemaType.equals("any")) schemaType = TYPE_ANY;

        if (Objects.equals(TYPE_ANY, schemaType) && propertySchema != null && propertySchema.has(PROPERTIES)) schemaType = TYPE_OBJECT;

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
            Set<String> requiredProperties = schemaRequired.toList().stream().map(String.class::cast).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            validateRequiredProperties(propertyNode, requiredProperties, String.join(", ", requiredProperties));
        }
    }

    private void validateArray(JSONObject propertySchema, JsonNode propertyNode) {
        JSONObject schemaItems = (propertySchema != null && propertySchema.has(ITEMS)) ? propertySchema.getJSONObject(ITEMS) : null;
        String itemsType = (schemaItems != null && schemaItems.has("type")) ? schemaItems.getString("type") : TYPE_ANY;
        if (itemsType == null || itemsType.trim().isEmpty() || itemsType.equals("any")) itemsType = TYPE_ANY;
        if (Objects.equals(TYPE_ANY, itemsType) && schemaItems != null && schemaItems.has(PROPERTIES)) itemsType = TYPE_OBJECT;

        if (TYPE_OBJECT.equals(itemsType)) {
            validateItems(propertyNode, itemsType).ifPresent(itemNode -> validateObject(schemaItems, itemNode));
        } else if (TYPE_ARRAY.equals(itemsType)) {
            validateItems(propertyNode, itemsType).ifPresent(itemNode -> validateArray(schemaItems, itemNode));
        } else {
            validateItems(propertyNode, itemsType);
        }
    }

}