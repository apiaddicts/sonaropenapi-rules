package apiaddicts.sonar.openapi.checks.regex;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.impl.MissingNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Rule(key = OAR112RegexCheck.KEY)
public class OAR112RegexCheck extends BaseCheck {

    public static final String KEY = "OAR112";
    private static final String NODE = "info/description";
    private static final String ERROR_MESSAGE = "The field must start with an uppercase letter.";
    private static final String VALIDATION = "^[A-Z].*";

    private static final String DESCRIPTION = "description";

    @RuleProperty(
        key = "Node",
        description = "Specify the sections of the OpenAPI document to be validated. This defines the scope for the regular expression validation.",
        defaultValue = NODE
    )
    private String nodes = NODE;

    @RuleProperty(
        key = "Error Message",
        description = "Enter a custom error message that will appear if the rule is violated.",
        defaultValue = ERROR_MESSAGE
    )
    private String errorMessage = ERROR_MESSAGE;

    @RuleProperty(
        key= "Validation",
        description= "Provide the regular expression that will be used to validate the specified sections of the OpenAPI document.",
        defaultValue = VALIDATION
    )
    private String valid = VALIDATION;

    String[] segments = NODE.split("/");

    String segment1 = segments.length > 0 ? segments[0] : null;
    String segment2 = segments.length > 1 ? segments[1] : null;
    String segment3 = segments.length > 2 ? segments[2] : null;
    String segment4 = segments.length > 3 ? segments[3] : null;
    String segment5 = segments.length > 4 ? segments[4] : null;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
            OpenApi3Grammar.ROOT,
            OpenApi3Grammar.PATHS,
            OpenApi3Grammar.OPERATION,
            OpenApi3Grammar.INFO,
            OpenApi3Grammar.RESPONSES,
            OpenApi3Grammar.PARAMETER,
            OpenApi3Grammar.COMPONENTS,
            OpenApi3Grammar.REQUEST_BODY,
            OpenApi3Grammar.SCHEMA,
            OpenApi3Grammar.SECURITY_SCHEME,
            OpenApi3Grammar.TAG,
            OpenApi3Grammar.SERVER,
            OpenApi3Grammar.EXTERNAL_DOC,
            OpenApi2Grammar.ROOT,
            OpenApi2Grammar.PATHS,
            OpenApi2Grammar.OPERATION,
            OpenApi2Grammar.INFO,
            OpenApi2Grammar.RESPONSES,
            OpenApi2Grammar.PARAMETER,
            OpenApi2Grammar.DEFINITIONS,
            OpenApi2Grammar.SCHEMA,
            OpenApi2Grammar.SECURITY_SCHEME,
            OpenApi2Grammar.TAG,
            OpenApi2Grammar.EXTERNAL_DOC

        );
    }

    @Override
    public void visitNode(JsonNode node) {
        String[] nodeSegments = nodes.split("/");
        List<String> pathSegments = Arrays.asList(nodeSegments);
        AstNodeType type = node.getType();

        boolean isPathRoot = nodeSegments.length > 1 && "paths".equals(nodeSegments[0]);
        boolean isMethod = isPathRoot && Arrays.asList("get", "post", "put", "patch", "delete")
                .contains(nodeSegments[1].toLowerCase());

        if (pathSegments.contains("info") && isType(type, OpenApi3Grammar.ROOT, OpenApi2Grammar.ROOT)) {
            handleInfoSection(node, pathSegments);
        } else if (pathSegments.contains("servers") && type.equals(OpenApi3Grammar.SERVER)) {
            handleServerNode(node, pathSegments);
        } else if (isMethod && isType(type, OpenApi3Grammar.OPERATION, OpenApi2Grammar.OPERATION)) {
            handleOperationsNode(node, pathSegments);
        } else if (pathSegments.contains("tags") && isType(type, OpenApi3Grammar.TAG, OpenApi2Grammar.TAG)) {
            handleTagsNode(node, pathSegments);
        } else if (pathSegments.contains("externalDocs") && isType(type, OpenApi3Grammar.EXTERNAL_DOC, OpenApi2Grammar.EXTERNAL_DOC)) {
            handleExternalDocsNode(node, pathSegments);
        } else if (isMethod && nodeSegments.length > 2 && "parameters".equals(nodeSegments[2])
                && isType(type, OpenApi3Grammar.PARAMETER, OpenApi2Grammar.PARAMETERS)) {
            handleParametersNode(node, pathSegments);
        }
    }

    private boolean isType(AstNodeType target, AstNodeType... types) {
        for (AstNodeType t : types) {
            if (t.equals(target)) return true;
        }
        return false;
    }

    private void handleInfoSection(JsonNode node, List<String> pathSegments) {
        JsonNode infoNode = node.get("info");
        validateSection(infoNode, pathSegments, Arrays.asList("title", DESCRIPTION, "termsOfService", "contact", "license", "version"));
    }

    private void handleServerNode(JsonNode serverNode, List<String> pathSegments) {
        validateSection(serverNode, pathSegments, Arrays.asList("url", DESCRIPTION));
    }

    private void handleOperationsNode(JsonNode operationsNode, List<String> pathSegments) {
        if ("responses".equals(segment3)) {
            JsonNode responsesNode = operationsNode.get("responses");
            if (responsesNode != null && !responsesNode.isMissing()) {
                String statusCodeToCheck = segment4;
                JsonNode responseNode = responsesNode.get(statusCodeToCheck);
                if (responseNode != null && !responseNode.isMissing()) {
                    JsonNode descriptionNode = responseNode.get(DESCRIPTION);
                    if (descriptionNode != null && !descriptionNode.isMissing() && !descriptionNode.getTokenValue().matches(valid)) {
                        addIssue(KEY, errorMessage, descriptionNode.key());
                    }
                }
            }
        }
        else{
            validateSection(operationsNode, pathSegments, Arrays.asList("summary", DESCRIPTION, "operationId"));
        }
    }

    private void handleParametersNode(JsonNode parametersNode, List<String> pathSegments){
        validateSection(parametersNode, pathSegments, Arrays.asList(DESCRIPTION));
    }
    private void handleTagsNode(JsonNode tagsNode, List<String> pathSegments) {
        validateSection(tagsNode, pathSegments, Arrays.asList("name", DESCRIPTION));
    }
    private void handleExternalDocsNode(JsonNode externalDocNode, List<String> pathSegments) {
        validateSection(externalDocNode, pathSegments, Arrays.asList("url", DESCRIPTION));
    }

    private void validateSection(JsonNode parentNode, List<String> pathSegments, List<String> keysToValidate) {
        for (String key : keysToValidate) {
            if (pathSegments.contains(key)) {
                validateNodeWithRegex(parentNode, key, valid);
            }
        }
    }

    private void validateNodeWithRegex(JsonNode parentNode, String childKey, String validation) {
        JsonNode childNode = parentNode.get(childKey);
        boolean validationAsBoolean = "true".equals(validation) || "false".equals(validation);

        if (validationAsBoolean && "false".equals(validation)) {
            if (!(childNode == null || childNode instanceof MissingNode)) {
                addIssue(KEY, errorMessage, childNode.key());
            }
        } else if (validationAsBoolean && "true".equals(validation)) {
            if (childNode == null || childNode instanceof MissingNode || childNode.getTokenValue().isEmpty()) {
                addIssue(KEY, "Expected to find a value but didn't.", parentNode.key());
            }
        } else {
            if (childNode != null && !(childNode instanceof MissingNode) && !childNode.getTokenValue().matches(validation)) {
                addIssue(KEY, errorMessage, childNode.key());
            }
        }
    }
}