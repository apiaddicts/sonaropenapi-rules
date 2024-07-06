package apiquality.sonar.openapi.checks.examples;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static apiquality.sonar.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR031ExamplesCheck.KEY)
public class OAR031ExamplesCheck extends BaseCheck {

    protected JsonNode externalRefNode = null;
    public static final String KEY = "OAR031";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi2Grammar.RESPONSES, OpenApi3Grammar.SCHEMA, OpenApi3Grammar.RESPONSES, OpenApi31Grammar.SCHEMA, OpenApi31Grammar.RESPONSES, OpenApi3Grammar.REQUEST_BODY, OpenApi31Grammar.REQUEST_BODY, OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PATH.equals(node.getType()) || OpenApi3Grammar.PATH.equals(node.getType()) || OpenApi31Grammar.PATH.equals(node.getType())) {
            visitPathNode(node);
        } else if (node.getType().equals(OpenApi2Grammar.RESPONSES) || node.getType().equals(OpenApi2Grammar.SCHEMA)) {
            visitV2Node(node);
        } else {
            visitV3Node(node);
        }
    }

    private void visitV2Node(JsonNode node) {
        AstNodeType type = node.getType();
        if (type.equals(OpenApi2Grammar.RESPONSES)) {
            List<JsonNode> responseCodes = node.properties().stream().collect(Collectors.toList());
            for (JsonNode responseNode : responseCodes) {
                if (!responseNode.key().getTokenValue().equals("204")) {
                    if (!responseNode.key().getTokenValue().equals("204")) {
                        boolean externalRefManagement = false;
                        if (isExternalRef(responseNode) && externalRefNode == null) {
                            externalRefNode = responseNode;
                            externalRefManagement = true;
                        }
                        responseNode = resolve(responseNode);
                        visitResponseV2Node(responseNode);
                        if (externalRefManagement) externalRefNode = null; 
                    }
                }
            }
        } else if (type.equals(OpenApi2Grammar.SCHEMA)) {
            visitSchemaNode(node);
        }
    }

    private void visitResponseV2Node(JsonNode node) {
        if (node.get("examples").isMissing()) {
            addIssue(KEY, translate("OAR031.error-response"), node.key());
        }
    }

    private void visitV3Node(JsonNode node) {
        AstNodeType type = node.getType();
        if (type.equals(OpenApi3Grammar.RESPONSES)) {
            List<JsonNode> responseCodes = node.properties().stream().collect(Collectors.toList());
            for (JsonNode responseNode : responseCodes) {
                if (!responseNode.key().getTokenValue().equals("204")) {
                    boolean externalRefManagement = false;
                    if (isExternalRef(responseNode) && externalRefNode == null) {
                        externalRefNode = responseNode;
                        externalRefManagement = true;
                    }
                    responseNode = resolve(responseNode);
                    visitRequestBodyOrResponseV3Node(responseNode);
                    if (externalRefManagement) externalRefNode = null; 
                } 
            }
        } else if (type.equals(OpenApi3Grammar.SCHEMA)) {
            visitSchemaNode(node);
        } else if (type.equals(OpenApi3Grammar.REQUEST_BODY)) {
            visitRequestBodyOrResponseV3Node(node);
        }
    }

    private void visitRequestBodyOrResponseV3Node(JsonNode node) {
        JsonNode content = node.at("/content");
        if (content.isMissing()) return;
        for (JsonNode mediaTypeNode : content.propertyMap().values()) {
            AstNodeType type = node.getType();
            JsonNode schemaNode = mediaTypeNode.get("schema");
            boolean hasSchemaExample = false;
            if (schemaNode.getType().equals(OpenApi3Grammar.SCHEMA) && !schemaNode.get("example").isMissing()) {
                hasSchemaExample = true;
            }
            if (!hasSchemaExample && mediaTypeNode.get("examples").isMissing() && mediaTypeNode.get("example").isMissing()) {
                if (type.equals(OpenApi3Grammar.REQUEST_BODY)) {
                    addIssue(KEY, translate("OAR031.error-request"), getTrueNode(node.key()));
                } else {
                    addIssue(KEY, translate("OAR031.error-response"), getTrueNode(node.key()));
                }
            }
        }
    }

    private void visitSchemaNode(JsonNode node) {
        JsonNode parentNode = (JsonNode) node.getParent().getParent();

        if (parentNode.getType().equals(OpenApi3Grammar.PARAMETER)) {
            if (node.get("example").isMissing() && parentNode.get("example").isMissing() && parentNode.get("examples").isMissing()) {
                addIssue(KEY, translate("OAR031.error-parameter"), parentNode);
            }
        } else if (parentNode.getType().equals(OpenApi3Grammar.SCHEMA_PROPERTIES) || 
            parentNode.getType().toString().equals("BLOCK_MAPPING") || parentNode.getType().toString().equals("FLOW_MAPPING")) {
            JsonNode type = getType(node);
            if (!isObjectType(type) && !type.isMissing() && !isArrayType(type)) {
                if (node.get("example").isMissing()) {
                    addIssue(KEY, translate("OAR031.error-property"), node.key());
                }
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
                content.propertyMap().forEach((mediaType, mediaTypeNode) -> { // estudiar función lambda
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
                    String key = entry.getKey();
                    JsonNode propertyNode = entry.getValue();
                    System.out.println("Propiedad: " + key);

                    // Busca el nodo 'example' en cada propiedad
                    JsonNode exampleNode = propertyNode.get("example");
                    if (exampleNode != null && !exampleNode.isMissing()) {
                        System.out.println("Encontrado 'example' en: " + key);
                        // Aquí añadirías la lógica para manejar la presencia incorrecta de 'example'
                    } else {
                        addIssue(KEY, translate("OAR031.error-property"), getTrueNode(propertyNode.key()));
                    }
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

    protected JsonNode getTrueNode(JsonNode node) {
        return externalRefNode == null ? node : externalRefNode;
    }
}
