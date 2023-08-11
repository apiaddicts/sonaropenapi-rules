package org.sonar.samples.openapi.checks.security;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Rule(key = OAR082BinaryOrByteFormatCheck.KEY)
public class OAR082BinaryOrByteFormatCheck extends AbstractPropertiesCheck {

    public static final String KEY = "OAR082";
    private static final String MESSAGE = "OAR082.error";
    private static final String FIELDS_TO_APPLY = "product,line,price";

    @RuleProperty(
            key = "fields-to-apply",
            description = "List of fields where the rule should be applied (separated by comma)",
            defaultValue = FIELDS_TO_APPLY
    )
    private String fieldsApply = FIELDS_TO_APPLY;

    private List<String> fieldsList = Arrays.asList(fieldsApply.split(","));

    @Override
    public void validate(String type, String format, String properties, JsonNode typeNode, JsonNode propertiesNode) {
        List<JsonNode> propertyChildren = propertiesNode.getJsonChildren();
        List<JsonNode> matchingProperties = new ArrayList<>();

        for (JsonNode propertyChild : propertyChildren) {
            String propertyName = propertyChild.key().getTokenValue();
            if (fieldsList.contains(propertyName)) {
                matchingProperties.add(propertyChild);
            }
        }

        for (JsonNode matchingProperty : matchingProperties) {
            JsonNode formatNode = matchingProperty.key().get("format");
            if (formatNode == null) {
                addIssue(KEY, translate(MESSAGE), matchingProperty.key());
            } else {
                String propertyFormat = formatNode.getTokenValue();
                if (!isValidStringFormat(propertyFormat)) {
                    addIssue(KEY, translate(MESSAGE), matchingProperty.key());
                }
            }
        }
    }
    

    private boolean isValidStringFormat(String format) {
        return "binary".equals(format) || "byte".equals(format);
    }
}