package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import apiaddicts.sonar.openapi.checks.schemas.AbstractExplicitResponseCheck;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Map;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR038StandardCreateResponseCheck.KEY)
public class OAR038StandardCreateResponseCheck extends AbstractExplicitResponseCheck {

    public static final String KEY = "OAR038";

    private static final String DATA_PROPERTY = "data";
    private static final String ERROR_PROPERTY = "error";

    @RuleProperty(
            key = "data-property",
            description = "Valid top-level property name for the standard response.",
            defaultValue = DATA_PROPERTY
    )
    private String dataNode = DATA_PROPERTY;

    public OAR038StandardCreateResponseCheck() {
        super(KEY, "201");
    }

    @Override
    protected void visitV2ExplicitNode(JsonNode node) {
        JsonNode schemaNode = node.get("schema");
        if (schemaNode.isMissing()) {
            addIssue(KEY, translate("OAR038.error-required-schema"), node.key());
            return;
        }

        schemaNode = resolve(schemaNode);
        Map<String, JsonNode> properties = getAllProperties(schemaNode);

        for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
            String propName = entry.getKey();
            if (dataNode.equals(propName) || ERROR_PROPERTY.equals(propName)) {
                Map<String, JsonNode> subProps = getAllProperties(resolve(entry.getValue()));
                if (subProps.isEmpty()) {
                    addIssue(KEY, translate("OAR038.error-required-one-property"), entry.getValue().key());
                }
            } else {
                addIssue(KEY, translate("OAR038.error", dataNode), entry.getValue().key());
            }
        }

        if (properties.isEmpty()) {
            addIssue(KEY, translate("OAR038.error", dataNode), schemaNode.key());
        }
    }
}
