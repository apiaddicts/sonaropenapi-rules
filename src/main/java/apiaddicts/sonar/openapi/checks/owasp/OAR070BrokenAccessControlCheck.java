package apiaddicts.sonar.openapi.checks.owasp;

import apiaddicts.sonar.openapi.checks.parameters.AbstractParameterCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

@Rule(key = OAR070BrokenAccessControlCheck.KEY)
public class OAR070BrokenAccessControlCheck extends AbstractParameterCheck {

    public static final String KEY = "OAR070";
    private static final String MESSAGE = "OAR070.error";

    @Override
    protected void visitParameterNode(JsonNode node) {

        JsonNode inNode = node.get("in");

        if (inNode == null || !"path".equals(inNode.getTokenValue())) {
            return;
        }

        JsonNode nameNode = node.get("name");
        JsonNode typeNode = node.get("type");
        JsonNode schemaNode = node.get("schema");

        boolean isNumericType =
                typeNode != null &&
                ("integer".equals(typeNode.getTokenValue()) ||
                 "number".equals(typeNode.getTokenValue()) ||
                 "float".equals(typeNode.getTokenValue()));

        if (!isNumericType && schemaNode != null) {

            JsonNode schemaTypeNode = schemaNode.get("type");

            isNumericType =
                    schemaTypeNode != null &&
                    ("integer".equals(schemaTypeNode.getTokenValue()) ||
                     "number".equals(schemaTypeNode.getTokenValue()) ||
                     "float".equals(schemaTypeNode.getTokenValue()));

            typeNode = schemaTypeNode;
        }

        if (nameNode != null && isNumericType) {
            addIssue(KEY, translate(MESSAGE), typeNode);
        }
    }
}