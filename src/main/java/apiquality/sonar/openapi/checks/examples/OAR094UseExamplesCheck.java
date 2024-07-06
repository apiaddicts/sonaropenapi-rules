package apiquality.sonar.openapi.checks.examples;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiquality.sonar.openapi.checks.BaseCheck;
import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR094UseExamplesCheck.KEY)
public class OAR094UseExamplesCheck extends BaseCheck {

    protected JsonNode externalRefNode= null;
    public static final String KEY = "OAR094";
    private static final String MESSAGE = "OAR094.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi31Grammar.ROOT, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType()) || OpenApi31Grammar.ROOT.equals(node.getType())) {
            deepSearchForExample(node);
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        }
    }

    private void deepSearchForExample(JsonNode node) {
        if (node.propertyMap().containsKey("example")) {
            addIssue(KEY, translate(MESSAGE), node.propertyMap().get("example").key());
            return; 
        }
        
        // Recurse into children
        for (JsonNode child : node.propertyMap().values()) {
            deepSearchForExample(child);
        }
    }

    private void visitPathNode(JsonNode node) {
        List<JsonNode> allResponses = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)) // operations
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
    
        JsonNode propertiesNode = schemaNode.get("properties");
        if (propertiesNode != null && !propertiesNode.isMissing() && propertiesNode.isObject()) {
            Map<String, JsonNode> properties = propertiesNode.propertyMap();
            if (!properties.isEmpty()) {
                for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
                    String key = entry.getKey();
                    JsonNode propertyNode = entry.getValue();
                    JsonNode exampleNode = propertyNode.get("example");
                    if (exampleNode != null && !exampleNode.isMissing()) {
                        addIssue(KEY, translate(MESSAGE), getTrueNode(exampleNode.key()));
                    } 
                }
            }
        }
    
        if (externalRefManagement) {
            externalRefNode = null;
        }
    }
    

    protected JsonNode getTrueNode (JsonNode node){
        return externalRefNode== null ? node : externalRefNode;
    }
}