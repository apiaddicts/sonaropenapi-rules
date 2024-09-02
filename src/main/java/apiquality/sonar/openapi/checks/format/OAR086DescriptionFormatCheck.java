package apiquality.sonar.openapi.checks.format;

import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiquality.sonar.openapi.checks.BaseCheck;
import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;



import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

@Rule(key = OAR086DescriptionFormatCheck.KEY)
public class OAR086DescriptionFormatCheck extends BaseCheck {

    protected JsonNode externalRefNode= null;
    public static final String KEY = "OAR086";
    private static final String MESSAGE = "OAR086.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi31Grammar.ROOT, OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi31Grammar.PATHS, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType()) || OpenApi31Grammar.ROOT.equals(node.getType())) {
            checkInfoDescription(node);
            checkDefinitionsDescription(node);
        }
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())|| OpenApi31Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        }
    }

    private void checkInfoDescription(JsonNode rootNode) {
        JsonNode infoNode = rootNode.get("info");
        if (infoNode != null) {
            checkDescriptionFormat(infoNode.get("description"));
        }
    }

    
    private void checkDefinitionsDescription(JsonNode rootNode) {
        JsonNode definitionsNode = rootNode.get("definitions");
        if (definitionsNode != null) {
            for (JsonNode definition : definitionsNode.propertyMap().values()) {
                checkDescriptionFormat(definition.get("description"));
            }
        }

        JsonNode componentsNode = rootNode.get("components");
        if (componentsNode != null && !componentsNode.isMissing()) {
            JsonNode schemasNode = componentsNode.get("schemas");
            if (schemasNode != null && !schemasNode.isMissing()) {
                for (JsonNode schema : schemasNode.propertyMap().values()) {
                    JsonNode schemaDescription = schema.get("description");
                    if (schemaDescription != null && !schemaDescription.isMissing()) {
                        checkDescriptionFormat(schemaDescription);
                    }
        
                    JsonNode properties = schema.get("properties");
                    if (properties != null && !properties.isMissing()) {
                        for (JsonNode property : properties.propertyMap().values()) {
                            JsonNode propertyDescription = property.get("description");
                            if (propertyDescription != null && !propertyDescription.isMissing()) {
                                checkDescriptionFormat(propertyDescription);
                            }
                        }
                    }
                }
            }
        }
    }

    private void visitPathNode(JsonNode node) {
        List<JsonNode> allResponses = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)) 
                .map(JsonNode::value)
                .flatMap(n -> n.properties().stream()) 
                .map(JsonNode::value)
                .flatMap(n -> n.properties().stream()) 
                .collect(Collectors.toList());
                for (JsonNode responseNode : allResponses) {
                    boolean externalRefManagement = false;
                        if (isExternalRef(responseNode) && externalRefNode == null) {
                            externalRefNode = responseNode;
                            externalRefManagement = true;
                        }
                    responseNode = resolve(responseNode);
        
                    if (responseNode.getType().equals(OpenApi2Grammar.RESPONSE)) {
                        visitSchemaNode(responseNode);
                    } else if (responseNode.getType().equals(OpenApi3Grammar.RESPONSE)) {
                        JsonNode content = responseNode.at("/content");
                        if (content.isMissing()) {
                            if (externalRefManagement) externalRefNode = null; 
                            continue;
                        }
                        content.propertyMap().forEach((mediaType, mediaTypeNode) -> { //estudiar funcion lamda
                            if (!mediaType.toLowerCase().contains("json")) return;
                            visitSchemaNode(mediaTypeNode);
                        });
                    }
                    if (externalRefManagement) externalRefNode = null; 
                }
    }

    private void visitSchemaNode(JsonNode responseNode) {
        JsonNode schemaNode = responseNode.value().get("schema");
    
        if (schemaNode.isMissing()) {
            return;
        }
    
        boolean externalRefManagement = false;
        if (isExternalRef(schemaNode) && externalRefNode == null) {
            externalRefNode = schemaNode;
            externalRefManagement = true;
        }
    
        schemaNode = resolve(schemaNode);
    
        Map<String, JsonNode> properties = schemaNode.propertyMap();
        if (!properties.isEmpty()) {
            for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
                String key = entry.getKey();
                JsonNode propertyNode = entry.getValue();
    
                if (key.contains("description")) {  
                    checkDescriptionFormat(propertyNode); 
                }
            }
        }
    
        if (externalRefManagement) {
            externalRefNode = null;
        }
    }
    
    
    

    

    protected void visitPathsNode(JsonNode pathsNode) {    
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {            
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                JsonNode operationDescription = operationNode.get("description");
                checkDescriptionFormat(operationDescription);
    
                JsonNode responsesNode = operationNode.get("responses");                
                if (responsesNode != null) {
                    for (JsonNode responseNode : responsesNode.propertyMap().values()) {
                        boolean externalRefManagement = false;
                        if (isExternalRef(responseNode) && externalRefNode == null) {
                            externalRefNode = responseNode;
                            externalRefManagement = true;
                        }
                        responseNode= resolve(responseNode);
                        JsonNode responseDescription = responseNode.get("description");
                        checkDescriptionFormat(responseDescription);
                        if (externalRefManagement) externalRefNode = null; 
                    }
                }
            }
        }
    }
    


    private void checkDescriptionFormat(JsonNode descriptionNode) {
        if (descriptionNode == null || descriptionNode.isMissing()) {
            return;
        }
    
        String description = descriptionNode.getTokenValue();
        description = description == null ? "" : description.trim();
    
        if (description.isEmpty()) {
            addIssue(KEY, translate(MESSAGE), getTrueNode(descriptionNode));
            return;
        }
    
        if (!Character.isUpperCase(description.charAt(0)) || !description.endsWith(".")) {
            addIssue(KEY, translate(MESSAGE), getTrueNode(descriptionNode));
        }
    }  
    
    protected JsonNode getTrueNode (JsonNode node){
        return externalRefNode== null ? node : externalRefNode;
    }

}