package org.sonar.samples.openapi.checks.resources;

import com.sonar.sslr.api.AstNode;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.samples.openapi.utils.JsonNodeUtils;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

public abstract class AbstractSchemaCheck extends BaseCheck {

    protected String key;

    public AbstractSchemaCheck(String key) {
        this.key = key;
    }

    protected Map<String, JsonNode> getAllProperties(JsonNode schemaNode) {
        final Map<String, JsonNode> properties = new HashMap<>();

        JsonNode propertiesNode = getProperties(schemaNode);
        if (!propertiesNode.isMissing()) {
            Map<String, JsonNode> baseProperties = getProperties(schemaNode).propertyMap();
            properties.putAll(baseProperties);
        }

        JsonNode allOfNode = schemaNode.get("allOf");
        if (!allOfNode.isMissing()) {
            allOfNode.elements().stream()
                    .map(JsonNodeUtils::resolve)
                    .forEach(node -> properties.putAll(getAllProperties(node)));
        }

        return properties;
    }

    protected Optional<JsonNode> validateProperty(Map<String, JsonNode> properties, String propertyName, String propertyType, JsonNode parentNode) {
        if (!properties.containsKey(propertyName)) {
            addIssue(key, translate("generic.property-missing", propertyName), parentNode);
            return Optional.empty();
        }
        JsonNode prop = properties.get(propertyName);
        return validateProperty(prop, propertyName, propertyType, parentNode);
    }

    protected Optional<JsonNode> validateProperty(JsonNode properties, String propertyName, String propertyType) {
        properties = resolve(properties);
        JsonNode prop = properties.get(propertyName);
        return validateProperty(prop, propertyName, propertyType, properties.key());
    }

    protected Optional<JsonNode> validateProperty(JsonNode prop, String propertyName, String propertyType, JsonNode parentNode) {
        if (prop.isMissing()) {
            addIssue(key, translate("generic.property-missing", propertyName), parentNode);
            return Optional.empty();
        } else {
            prop = resolve(prop);
            JsonNode type = getType(prop);
            if (!isType(type, propertyType)) {
                JsonNode errorNode = (type.isMissing() ? prop : type.key());
                addIssue(key, translate("generic.property-wrong-type", propertyName, propertyType), errorNode);
            }
            return Optional.of(prop);
        }
    }

    protected void validateRequiredProperties(JsonNode schema, Set<String> requiredValues, String requiredStr) {
        JsonNode required = getRequired(schema);
        Set<String> requiredProperties = getRequiredValues(required);
        if (!requiredProperties.containsAll(requiredValues)) {
            JsonNode errorNode = (required.isMissing() ? schema : required.key());
            addIssue(key, translate("generic.required-properties", requiredStr), errorNode);
        }
    }

    protected Optional<JsonNode> validateItems(JsonNode prop, String iType) {
        prop = resolve(prop);
        JsonNode type = getType(prop);
        if (isType(type, TYPE_ARRAY)) {
            JsonNode itemsSchema = prop.get("items");
            itemsSchema = resolve(itemsSchema);
            String propertyName = prop.key().getTokenValue();
            if (itemsSchema.isMissing()) {
                addIssue(key, translate("generic.property-items-missing", propertyName, iType), prop.key());
            } else {
                JsonNode itemsType = getType(itemsSchema);
                if (!isType(itemsType, iType)) {
                    JsonNode errorNode = (itemsType.isMissing() ? itemsSchema : itemsType.key());
                    addIssue(key, translate("generic.property-items-wrong-type", propertyName, iType), errorNode);
                    return Optional.empty();
                } else {
                    return Optional.of(itemsSchema);
                }
            }
        }
        return Optional.empty();
    }

    protected void validateEnumValues(JsonNode property, Set<String> expected) {
        Set<String> found = getEnumValues(property);
        if (!expected.equals(found)) {
            String propertyName = property.key().getTokenValue();
            String values = expected.stream().sorted().collect(Collectors.joining(", "));
            addIssue(key, translate("generic.enum-values", propertyName, values), property.key());
        }
    }

    private Set<String> getEnumValues(JsonNode schema) {
        return schema.get("enum").elements()
                .stream().map(AstNode::getTokenValue).collect(Collectors.toSet());
    }
}
