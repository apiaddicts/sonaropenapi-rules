package apiquality.sonar.openapi.checks.format;

import apiquality.sonar.openapi.checks.BaseCheck;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import static apiquality.sonar.openapi.utils.JsonNodeUtils.isExternalRef;
import static apiquality.sonar.openapi.utils.JsonNodeUtils.resolve;

@Rule(key = OAR113CustomFieldCheck.KEY)
public class OAR113CustomFieldCheck extends BaseCheck {

    public static final String KEY = "OAR113";
    private static final String DEFAULT_FIELD_NAME = "x-custom-example";
    private static final String DEFAULT_FIELD_LOCATION = "path";

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
        String[] locations = fieldLocation.split(",");
        if (OpenApi3Grammar.PARAMETER.equals(node.getType()) || OpenApi2Grammar.PARAMETER.equals(node.getType()) ){
            System.out.println(node.key().getTokenValue());

        }
        manageOperationAndResponse(node,locations);
        analyzePathNodes(node,locations);


    }

    private void manageOperationAndResponse(JsonNode node, String[] locations){
        if (OpenApi2Grammar.OPERATION.equals(node.getType()) || OpenApi3Grammar.OPERATION.equals(node.getType()) || OpenApi31Grammar.OPERATION.equals(node.getType())){
            if (node.key().getTokenValue().equals("get") && (Arrays.asList(locations).contains("operation_get") || Arrays.asList(locations).contains("operation") )){
                checkRequiredFieldInNode(node);
            }
            if (node.key().getTokenValue().equals("post") && (Arrays.asList(locations).contains("operation_post") || Arrays.asList(locations).contains("operation") )){
                checkRequiredFieldInNode(node);
            }
        }
        if (OpenApi2Grammar.RESPONSE.equals(node.getType()) || OpenApi3Grammar.RESPONSE.equals(node.getType()) || OpenApi31Grammar.RESPONSE.equals(node.getType())){
            if (node.key().getTokenValue().equals("200") && (Arrays.asList(locations).contains("response_200") || Arrays.asList(locations).contains("response"))){
                checkRequiredFieldInNode(node);
            }
            if (node.key().getTokenValue().equals("400") && (Arrays.asList(locations).contains("response_400") || Arrays.asList(locations).contains("response"))){
                checkRequiredFieldInNode(node);
            }
        }
        if(OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())){
            if(Arrays.asList(locations).contains("path"))
                checkRequiredFieldInNode(node);
        }
        if(OpenApi2Grammar.SCHEMA.equals(node.getType()) || OpenApi3Grammar.SCHEMA.equals(node.getType()) || OpenApi31Grammar.SCHEMA.equals(node.getType())){
            if(Arrays.asList(locations).contains("schema"))
                checkRequiredFieldInNode(node);
        }
        if(OpenApi2Grammar.HEADER.equals(node.getType()) || OpenApi3Grammar.HEADER.equals(node.getType()) || OpenApi31Grammar.HEADER.equals(node.getType())){
            if(Arrays.asList(locations).contains("header"))
                checkRequiredFieldInNode(node);
        }
        if(OpenApi2Grammar.PARAMETER.equals(node.getType()) || OpenApi3Grammar.PARAMETER.equals(node.getType()) || OpenApi31Grammar.PARAMETER.equals(node.getType())){
            if(Arrays.asList(locations).contains("parameter"))
                checkRequiredFieldInNode(node);
        }

    }

    private void  analyzePathNodes(JsonNode node, String[] locations){
        if(Arrays.asList(locations).contains(node.key().getTokenValue())){
            checkRequiredFieldInNode(node);
        }
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
