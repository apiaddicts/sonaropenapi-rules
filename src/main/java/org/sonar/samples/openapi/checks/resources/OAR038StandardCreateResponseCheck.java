package org.sonar.samples.openapi.checks.resources;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.Map;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR038StandardCreateResponseCheck.KEY)
public class OAR038StandardCreateResponseCheck extends AbstractExplicitResponseCheck {

    public static final String KEY = "OAR038";

    private static final String DATA_NODE = "payload";

    @RuleProperty(
            key = "data-node",
            description = "Data node name in standard response.",
            defaultValue = DATA_NODE
    )
    private String dataNode = DATA_NODE;

    public OAR038StandardCreateResponseCheck() {
        super(KEY, "201");
    }

    @Override
    protected void visitV2ExplicitNode(JsonNode node) {
        JsonNode schemaNode = node.get("schema");
        if (schemaNode.isMissing()) {
            addIssue(KEY, translate("OAR038.error-required-schema"), node.key());
        } else {
            schemaNode = resolve(schemaNode);

            Map<String, JsonNode> properties = getAllProperties(schemaNode);

            validateProperty(properties, dataNode, TYPE_ANY, schemaNode.key())
                    .ifPresent(this::validateData);
        }
    }

    private void validateData(JsonNode data) {
        JsonNode properties = getProperties(data);
        if (properties.properties().isEmpty()) {
            addIssue(KEY, translate("OAR038.error-required-ope-property"), data.key());
        }
    }
}
