package apiaddicts.sonar.openapi.checks.owasp;

import apiaddicts.sonar.openapi.checks.parameters.AbstractParameterCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isType;

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
                isType(typeNode, "integer") ||
                isType(typeNode, "number") ||
                isType(typeNode, "float");

        if (!isNumericType && schemaNode != null) {

            JsonNode schemaTypeNode = schemaNode.get("type");

            isNumericType =
                    isType(schemaTypeNode, "integer") ||
                    isType(schemaTypeNode, "number") ||
                    isType(schemaTypeNode, "float");

            typeNode = schemaTypeNode;
        }

        if (nameNode != null && isNumericType) {
            addIssue(KEY, translate(MESSAGE), typeNode);
        }
    }
}