package apiquality.sonar.openapi.checks.schemas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import java.util.Set;

@Rule(key = OAR108SchemaValidatorCheck.KEY)
public class OAR108SchemaValidatorCheck extends BaseCheck {

    public static final String KEY = "OAR108";
    private static final String MESSAGE = "OAR108.error";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.isObject()) {
            for (JsonNode pathNode : node.propertyMap().values()) {
                for (JsonNode operationNode : pathNode.propertyMap().values()) {
                    JsonNode responsesNode = operationNode.get("responses");
                    System.out.println("SYSOO: " + responsesNode);
                    if (responsesNode != null && responsesNode.isObject()) {
                        for (JsonNode responseNode : responsesNode.propertyMap().values()) {
                            JsonNode schemaNode = responseNode.get("schema");
                            JsonNode examplesNode = responseNode.get("examples");
    
                            if (schemaNode != null && examplesNode != null) {
                                JsonNode exampleNode = examplesNode.get("application/json");
                                if (exampleNode != null) {
                                    validateSchema(schemaNode, exampleNode);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void validateSchema(JsonNode schemaNode, JsonNode exampleNode) {
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        System.out.println("Schema JSON332: ");

        try {
            String schemaJson = schemaNode.toString();
            String exampleJson = exampleNode.toString();
    
            // Imprimir las representaciones JSON para depuración
            System.out.println("Schema JSON: " + schemaJson);
            System.out.println("Example JSON: " + exampleJson);
    
            com.fasterxml.jackson.databind.JsonNode jacksonSchemaNode = mapper.readTree(schemaJson);
            com.fasterxml.jackson.databind.JsonNode jacksonExampleNode = mapper.readTree(exampleJson);
    
            JsonSchema schema = schemaFactory.getSchema(jacksonSchemaNode);
            Set<ValidationMessage> validationResult = schema.validate(jacksonExampleNode);
    
            if (!validationResult.isEmpty()) {
                addIssue(KEY, translate(MESSAGE), schemaNode.key());
            }
        } catch (Exception e) {
            // Manejar excepciones
            e.printStackTrace();
        }
    }
}