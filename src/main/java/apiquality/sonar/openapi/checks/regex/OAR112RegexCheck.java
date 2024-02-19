package apiquality.sonar.openapi.checks.regex;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.impl.MissingNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Rule(key = OAR112RegexCheck.KEY)
public class OAR112RegexCheck extends BaseCheck {

    public static final String KEY = "OAR112";
    private static final String NODE = "externalDocs/url";
    private static final String ERROR_MESSAGE = "The field must start with an uppercase letter.";
    private static final String EXPREG_DEFAULT = "^https://.*";
    
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
        key= "Regex",
        description= "Provide the regular expression that will be used to validate the specified sections of the OpenAPI document.",
        defaultValue = EXPREG_DEFAULT
    )
    private String expReg = EXPREG_DEFAULT;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
            OpenApi3Grammar.ROOT,
            OpenApi3Grammar.PATHS,
            OpenApi3Grammar.OPERATION,
            OpenApi3Grammar.INFO,
            OpenApi3Grammar.RESPONSES,
            OpenApi3Grammar.PARAMETER,
            OpenApi3Grammar.REQUEST_BODY,
            OpenApi3Grammar.SCHEMA,
            OpenApi3Grammar.SECURITY_SCHEME,
            OpenApi3Grammar.TAG,
            OpenApi3Grammar.SERVER,
            OpenApi3Grammar.EXTERNAL_DOC
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        List<String> pathSegments = Arrays.asList(nodes.split("/"));
        
        if (pathSegments.contains("info") && OpenApi3Grammar.ROOT.equals(node.getType())) {
            handleInfoSection(node, pathSegments);
        } else if (pathSegments.contains("servers") && OpenApi3Grammar.SERVER.equals(node.getType())) {
            handleServerNode(node, pathSegments); 
        } else if ((pathSegments.contains("get") || pathSegments.contains("post") || pathSegments.contains("put") || pathSegments.contains("patch") || pathSegments.contains("delete")) && OpenApi3Grammar.OPERATION.equals(node.getType())) {
            handleOperationsNode(node, pathSegments);
        } else if ((pathSegments.contains("tags")) && OpenApi3Grammar.TAG.equals(node.getType())) {
            handleTagsNode(node, pathSegments);
        } else if ((pathSegments.contains("externalDocs")) && OpenApi3Grammar.EXTERNAL_DOC.equals(node.getType())) {
            handleExternalDocsNode(node, pathSegments);
        }
    }

    private void handleInfoSection(JsonNode node, List<String> pathSegments) {
        JsonNode infoNode = node.get("info");
        validateSection(infoNode, pathSegments, Arrays.asList("title", "description", "termsOfService", "contact", "license", "version"));
    }

    private void handleServerNode(JsonNode serverNode, List<String> pathSegments) {
        validateSection(serverNode, pathSegments, Arrays.asList("url", "description"));
    }

    private void handleOperationsNode(JsonNode operationsNode, List<String> pathSegments) {
        validateSection(operationsNode, pathSegments, Arrays.asList("summary", "description"));   
    }
    private void handleTagsNode(JsonNode tagsNode, List<String> pathSegments) {
        validateSection(tagsNode, pathSegments, Arrays.asList("name", "description"));   
    }
    private void handleExternalDocsNode(JsonNode externalDocNode, List<String> pathSegments) {
        validateSection(externalDocNode, pathSegments, Arrays.asList("url", "description"));   
    }

    private void validateSection(JsonNode parentNode, List<String> pathSegments, List<String> keysToValidate) {
        for (String key : keysToValidate) {
            if (pathSegments.contains(key)) {
                validateNodeWithRegex(parentNode, key, expReg);
            }
        }
    }

    private void validateNodeWithRegex(JsonNode parentNode, String childKey, String regex) {
        JsonNode childNode = parentNode.get(childKey);
        if (childNode == null || childNode instanceof MissingNode) {
            return;
        }
        
        if (!childNode.getTokenValue().matches(regex)) {
            addIssue(KEY, errorMessage, childNode.key());
        }
    }
}