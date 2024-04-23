package apiquality.sonar.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR068PascalCaseNamingConventionCheck.KEY)
public class OAR068PascalCaseNamingConventionCheck extends AbstractNamingConventionCheck {

    protected JsonNode externalRefNode= null;
    public static final String KEY = "OAR068";
    private static final String MESSAGE = "OAR068.error";
    private static final String NAMING_CONVENTION = PASCAL_CASE;

    public OAR068PascalCaseNamingConventionCheck() {
        super(KEY, MESSAGE, NAMING_CONVENTION);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
            System.out.println("HOLA");
        }
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
            System.out.println("ADIOS");

        }
    }

    private void visitPathsNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                JsonNode parametersNode = operationNode.get("parameters");
                if (!parametersNode.isMissing()) {
                    for (JsonNode parameterNode : parametersNode.elements()) {
                        if ("body".equals(parameterNode.get("in").getTokenValue())) {
                            visitParameterNode(parameterNode);
                        }
                    }
                }
    
                JsonNode requestBodyNode = operationNode.get("requestBody");
                if (!requestBodyNode.isMissing()) {
                    visitRequestBodyNode(requestBodyNode);
                }
    
                JsonNode responsesNode = operationNode.get("responses");
                if (!responsesNode.isMissing()) {
                    visitResponsesNode(responsesNode);
                }
            }
        }
    }

    private void visitParameterNode(JsonNode parameterNode) {
        JsonNode inNode = parameterNode.get("in");
        if (!inNode.isMissing() && "body".equals(inNode.getTokenValue())) {
            JsonNode schemaNode = parameterNode.get("schema");
            if (!schemaNode.isMissing()) {
                visitSchemaNode(schemaNode);
            }
        } else {
            JsonNode nameNode = parameterNode.get("name");
            if (!nameNode.isMissing()) {
                String name = nameNode.getTokenValue();
                validateNamingConvention(name, nameNode);
            }
        }
    }
    

    private void visitRequestBodyNode(JsonNode requestBodyNode) {
        JsonNode contentNode = requestBodyNode.get("content");
        if (!contentNode.isMissing()) {
            for (JsonNode mediaTypeNode : contentNode.propertyMap().values()) {
                JsonNode schemaNode = mediaTypeNode.get("schema");
                if (!schemaNode.isMissing()) {
                    visitSchemaNode(schemaNode);
                }
            }
        }
    }

    private void visitResponsesNode(JsonNode responsesNode) {
        for (JsonNode responseNode : responsesNode.propertyMap().values()) {
            JsonNode contentNode = responseNode.get("content");
            if (!contentNode.isMissing()) {
                for (JsonNode mediaTypeNode : contentNode.propertyMap().values()) {
                    JsonNode schemaNode = mediaTypeNode.get("schema");
                    if (!schemaNode.isMissing()) {
                        visitSchemaNode(schemaNode);
                    }
                }
            } else {
                JsonNode schemaNode = responseNode.get("schema");
                if (!schemaNode.isMissing()) {
                    visitSchemaNode(schemaNode);
                }
            }
        }
    }
    
        private void visitSchemaNode(JsonNode schemaNode) {
            Map<String, JsonNode> properties = schemaNode.propertyMap();
            if (properties.containsKey("properties")) {
                Map<String, JsonNode> schemaProperties = schemaNode.get("properties").propertyMap();
                for (JsonNode property : schemaProperties.values()) {
                    JsonNode nameNode = property.key();
                    String name = nameNode.getTokenValue();
                    validateNamingConvention(name, nameNode);
                }
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
                            visitSchemaNode2(responseNode);
                        } else if (responseNode.getType().equals(OpenApi3Grammar.RESPONSE)) {
                            JsonNode content = responseNode.at("/content");
                            if (content.isMissing()) {
                                if (externalRefManagement) externalRefNode = null; 
                                continue;
                            }
                            content.propertyMap().forEach((mediaType, mediaTypeNode) -> { //estudiar funcion lamda
                                if (!mediaType.toLowerCase().contains("json")) return;
                                visitSchemaNode2(mediaTypeNode);
                            });
                        }
                        if (externalRefManagement) externalRefNode = null; 
                    }
        }
    
        private void visitSchemaNode2(JsonNode responseNode) {
            JsonNode schemaNode = responseNode.value().get("schema");
        
            if (schemaNode.isMissing()) {
                System.out.println("visitSchemaNode: El nodo del esquema no está disponible.");
                return;
            }
        
            boolean externalRefManagement = false;
            if (isExternalRef(schemaNode) && externalRefNode == null) {
                externalRefNode = schemaNode;
                externalRefManagement = true;
                System.out.println("visitSchemaNode: Gestionando referencia externa para el esquema.");
            }
        
            System.out.println("visitSchemaNode: Resolviendo el nodo del esquema.");
            schemaNode = resolve(schemaNode);
        
            // Inspecciona si el nodo del esquema tiene propiedades
            JsonNode propertiesNode = schemaNode.get("properties");
            if (propertiesNode != null && !propertiesNode.isMissing() && propertiesNode.isObject()) {
                Map<String, JsonNode> properties = propertiesNode.propertyMap();
                if (!properties.isEmpty()) {
                    for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
                        String propertyName = entry.getKey();
                        JsonNode propertyNode = entry.getValue();
                        System.out.println("Propiedad: " + propertyName);  // Imprime el nombre de cada propiedad
        
                        // Realiza operaciones específicas con cada nombre de propiedad
                        validateNamingConvention(propertyName, getTrueNode(propertyNode));
                    }
                } else {
                    System.out.println("visitSchemaNode: No properties found in properties node.");
                }
            } else {
                System.out.println("visitSchemaNode: El nodo de propiedades no está disponible o no contiene propiedades.");
            }
        
            if (externalRefManagement) {
                externalRefNode = null;
                System.out.println("visitSchemaNode: Reinicio de la gestión de la referencia externa.");
            }
        }
        protected JsonNode getTrueNode (JsonNode node){
            return externalRefNode== null ? node : externalRefNode;
        }
    }