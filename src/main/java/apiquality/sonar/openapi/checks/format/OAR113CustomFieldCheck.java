package apiquality.sonar.openapi.checks.format;

import apiquality.sonar.openapi.checks.BaseCheck;
import java.util.Set;

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
    protected JsonNode externalRefNode= null;
    private static final String MESSAGE = "OAR113.error";
    private static final String DEFAULT_FIELD_NAME = "x-custom-example";
    private static final String DEFAULT_FIELD_LOCATION = "info";

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
                OpenApi2Grammar.ROOT,
                OpenApi2Grammar.INFO,
                OpenApi2Grammar.PATHS,
                OpenApi2Grammar.DEFINITIONS,
                OpenApi3Grammar.ROOT,
                OpenApi3Grammar.INFO,
                OpenApi3Grammar.PATHS,
                OpenApi3Grammar.COMPONENTS,
                OpenApi31Grammar.ROOT,
                OpenApi31Grammar.INFO,
                OpenApi31Grammar.PATHS,
                OpenApi31Grammar.COMPONENTS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType())) {
            if(fieldLocation.equals("components")){
                fieldLocation = "definitions";
            }
            checkRequiredFieldInNode(node);
        }else if (OpenApi3Grammar.ROOT.equals(node.getType()) || OpenApi31Grammar.ROOT.equals(node.getType())){
            if(fieldLocation.equals("definitions")){
                fieldLocation = "components";
            }
            checkRequiredFieldInNode(node);
        }

    }

    private void checkRequiredFieldInNode(JsonNode rootNode) {
        JsonNode targetNode = resolveTargetNode(rootNode, fieldLocation);

        boolean externalRefManagement = false;
        if (isExternalRef(rootNode) && externalRefNode == null) {
            externalRefNode = rootNode;
            externalRefManagement = true;
        }

        targetNode = resolve(targetNode);

        if (targetNode == null || targetNode.isMissing()) {
            addIssue(KEY, translate("OAR113.error-found",fieldLocation), targetNode);
            return;
        }

        if (isExtension(fieldName)) {
            checkExtension(targetNode, fieldName);
        } else {
            checkStandardField(targetNode, fieldName);
        }
        if (externalRefManagement) externalRefNode = null;
    }

    private JsonNode resolveTargetNode(JsonNode rootNode, String location) {
        if (location == null || location.isEmpty()) {
            return rootNode;
        }
        String[] pathParts = location.split("\\.");
        JsonNode currentNode = rootNode;

        for (String part : pathParts) {

            if (currentNode == null) {
                return null;
            }
            currentNode = currentNode.get(part);
        }
        return currentNode;
    }


    private JsonNode getNodesV2(JsonNode node, String location){
        JsonNode currentNode = node;
        currentNode = currentNode.get(location);
        return currentNode;
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
