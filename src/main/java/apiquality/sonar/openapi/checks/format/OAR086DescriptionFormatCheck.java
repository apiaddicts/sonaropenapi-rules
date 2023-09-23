package apiquality.sonar.openapi.checks.format;

import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import com.google.common.collect.ImmutableSet;

@Rule(key = OAR086DescriptionFormatCheck.KEY)
public class OAR086DescriptionFormatCheck extends BaseCheck {

    public static final String KEY = "OAR086";
    private static final String MESSAGE = "OAR086.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.ROOT, OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS, OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.ROOT.equals(node.getType()) || OpenApi3Grammar.ROOT.equals(node.getType())) {
            checkInfoDescription(node);
            checkDefinitionsDescription(node);
        }
        if (OpenApi2Grammar.PATHS.equals(node.getType()) || OpenApi3Grammar.PATHS.equals(node.getType())) {
            visitPathsNode(node);
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
        
        // This is for OpenAPI 3.0's 'components/schemas'
        JsonNode componentsNode = rootNode.get("components");
        if (componentsNode != null) {
            JsonNode schemasNode = componentsNode.get("schemas");
            if (schemasNode != null) {
                for (JsonNode schema : schemasNode.propertyMap().values()) {
                    checkDescriptionFormat(schema.get("description"));
                }
            }
        }
    }

    protected void visitPathsNode(JsonNode pathsNode) {
        for (JsonNode pathNode : pathsNode.propertyMap().values()) {
            for (JsonNode operationNode : pathNode.propertyMap().values()) {
                checkDescriptionFormat(operationNode.get("description"));

                // Check descriptions in responses
                JsonNode responsesNode = operationNode.get("responses");
                if (responsesNode != null) {
                    for (JsonNode responseNode : responsesNode.propertyMap().values()) {
                        checkDescriptionFormat(responseNode.get("description"));
                    }
                }
            }
        }
    }

    private void checkDescriptionFormat(JsonNode descriptionNode) {
        boolean hasValidLength = false;
    
        if (descriptionNode != null && !descriptionNode.isMissing()) {
            String description = descriptionNode.getTokenValue();
            
            // Comprobar la longitud de la cadena
            hasValidLength = (description != null && description.length() > 0);
            
            // Si la longitud de la cadena es 0, añadir un problema y regresar
            if (!hasValidLength) {
                addIssue(KEY, translate(MESSAGE), descriptionNode);
                return;
            }
    
            // Comprobar otros criterios de formato
            if (!description.startsWith(Character.toString(description.charAt(0)).toUpperCase()) ||
                !description.endsWith(".")) {
                addIssue(KEY, translate(MESSAGE), descriptionNode);
            }
        }
    }
}