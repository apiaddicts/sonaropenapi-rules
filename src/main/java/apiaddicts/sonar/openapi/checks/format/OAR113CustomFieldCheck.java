package apiaddicts.sonar.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.BaseCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

@Rule(key = OAR113CustomFieldCheck.KEY)
public class OAR113CustomFieldCheck extends BaseCheck {

    public static final String KEY = "OAR113";
    private static final String DEFAULT_FIELD_NAME = "x-custom-example";
    private static final String DEFAULT_FIELD_LOCATION = "path,operation_get,response_200";

    @RuleProperty(
            key = "fieldName",
            description = "Name of the field or extension to validate",
            defaultValue = DEFAULT_FIELD_NAME
    )
    private String fieldName = DEFAULT_FIELD_NAME;

    @RuleProperty(
            key = "fieldLocation",
            description = "Location of the field in the OpenAPI document (e.g., info, paths, components)",
            defaultValue = DEFAULT_FIELD_LOCATION
    )
    private String fieldLocation = DEFAULT_FIELD_LOCATION;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.PATHS,
                OpenApi2Grammar.PATH,
                OpenApi2Grammar.OPERATION,
                OpenApi2Grammar.PARAMETER,
                OpenApi2Grammar.RESPONSE,
                OpenApi2Grammar.SCHEMA,
                OpenApi2Grammar.HEADER,
                OpenApi3Grammar.PATHS,
                OpenApi3Grammar.PATH,
                OpenApi3Grammar.COMPONENTS,
                OpenApi3Grammar.OPERATION,
                OpenApi3Grammar.PARAMETER,
                OpenApi3Grammar.RESPONSE,
                OpenApi3Grammar.SCHEMA,
                OpenApi3Grammar.HEADER,
                OpenApi31Grammar.OPERATION,
                OpenApi31Grammar.PATHS,
                OpenApi31Grammar.PATH,
                OpenApi31Grammar.COMPONENTS,
                OpenApi31Grammar.PARAMETER,
                OpenApi31Grammar.RESPONSE,
                OpenApi31Grammar.SCHEMA,
                OpenApi31Grammar.HEADER
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        Set locations = new HashSet<>(Arrays.asList(fieldLocation.split(",")));
        manageOperationAndResponse(node,locations);
    }

    private void manageOperationAndResponse(JsonNode node, Set<String> locations) {
        AstNodeType type = node.getType();
        String nodeKey = node.key().getTokenValue();

        if (isType(type, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION)) {
            String op = nodeKey.toLowerCase();
            if (locations.contains("operation") || locations.contains("operation_" + op)) {
                checkRequiredFieldInNode(node);
            }
        } else if (isType(type, OpenApi2Grammar.RESPONSE, OpenApi3Grammar.RESPONSE, OpenApi31Grammar.RESPONSE)) {
            if (locations.contains("response") || locations.contains("response_" + nodeKey)) {
                checkRequiredFieldInNode(node);
            }
        }

        else if (isType(type, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH)) verifyParameter(node, locations, "path");
        else if (isType(type, OpenApi2Grammar.SCHEMA, OpenApi3Grammar.SCHEMA, OpenApi31Grammar.SCHEMA)) verifyParameter(node, locations, "schema");
        else if (isType(type, OpenApi2Grammar.HEADER, OpenApi3Grammar.HEADER, OpenApi31Grammar.HEADER)) verifyParameter(node, locations, "header");
        else if (isType(type, OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER, OpenApi31Grammar.PARAMETER)) verifyParameter(node, locations, "parameter");

        if (locations.contains(nodeKey)) {
            checkRequiredFieldInNode(node);
        }
    }

    private void verifyParameter(JsonNode node,Set<String> locations ,String target){
        if(locations.contains(target))
            checkRequiredFieldInNode(node);
    }

    private void checkRequiredFieldInNode(JsonNode rootNode) {
        if (isExtension(fieldName)) {
            checkExtension(rootNode, fieldName);
        } else {
            checkStandardField(rootNode, fieldName);
        }
    }


    private boolean isExtension(String field) {
        return field != null && field.startsWith("x-");
    }

    private boolean isType(AstNodeType nodeType, AstNodeType... types) {
        return Arrays.asList(types).contains(nodeType);
    }

    private void checkExtension(JsonNode node, String extension) {
        boolean extensionExists = node.propertyMap().containsKey(extension);

        if (!extensionExists) {
            addIssue(KEY, translate("OAR113.error", extension), node.key());
        }
    }

    private void checkStandardField(JsonNode node, String field) {
        JsonNode fieldNode = node.get(field);
        if (fieldNode == null || fieldNode.isMissing()) {
            addIssue(KEY, translate("OAR113.error", field), node.key());
        }
    }

}
