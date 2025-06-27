package apiaddicts.sonar.openapi.checks.format;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isExternalRef;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.resolve;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.HashSet;
import java.util.Set;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR115VerifyRequiredFields.KEY)
public class OAR115VerifyRequiredFields extends BaseCheck {
 public static final String KEY = "OAR115";

  protected JsonNode externalRefNode = null;


 @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA,OpenApi2Grammar.RESPONSE, OpenApi3Grammar.SCHEMA,OpenApi31Grammar.SCHEMA,OpenApi3Grammar.RESPONSE, OpenApi31Grammar.RESPONSE);
    }

    @Override
    public void visitNode(JsonNode node) {
        if(node.getType() == OpenApi3Grammar.RESPONSE || node.getType() == OpenApi31Grammar.RESPONSE ){
            JsonNode content = node.get("content");
            JsonNode json = content.get("application/json");
            JsonNode schema = json.get("schema");
            resolveExteralRef(schema);
        }else if(node.getType() == OpenApi2Grammar.RESPONSE){
            JsonNode schema = node.get("schema");
            resolveExteralRef(schema);
        } else {
            verifyTypeObject(node);
        }
    }


    public void resolveExteralRef(JsonNode node) {
        if(!"null".equals(node.getTokenValue())){
            boolean externalRefManagement = false;
            if (isExternalRef(node) && externalRefNode == null) {
              externalRefNode = node;
              externalRefManagement = true;
            }

            node = resolve(node);
            validateRequiredFields(node);
            if (externalRefManagement) externalRefNode = null;
        }
    }

    public void verifyTypeObject(JsonNode node){
        JsonNode typeNode = node.get("type");
        if (typeNode != null && "object".equals(typeNode.getTokenValue())) {
            validateRequiredFields(node);
        }
    }


    private void validateRequiredFields(JsonNode objectNode) {
        JsonNode requiredNode = objectNode.get("required");
        JsonNode propertiesNode = objectNode.get("properties");

        if (requiredNode == null || !requiredNode.isArray()) return;

        Set<String> properties = new HashSet<>();
        if (propertiesNode != null) {
            for (JsonNode property : propertiesNode.getJsonChildren()) {
                String propertyName = property.key().getTokenValue();
                if(!"null".equals(propertyName)){
                    properties.add(propertyName); 
                }
            }
        }

        for (JsonNode requiredField : requiredNode.elements()) {
            String requiredName = requiredField.getTokenValue();
            if (!properties.contains(requiredName)) {
                addIssue(KEY,
                         translate("OAR115.error"),
                         requiredField);
            }
        }
    }
}
