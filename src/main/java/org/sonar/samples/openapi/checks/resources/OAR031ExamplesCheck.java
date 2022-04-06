package org.sonar.samples.openapi.checks.resources;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR031ExamplesCheck.KEY)
public class OAR031ExamplesCheck extends BaseCheck {

    public static final String KEY = "OAR031";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi2Grammar.OPERATION, OpenApi3Grammar.SCHEMA, OpenApi3Grammar.OPERATION, OpenApi3Grammar.REQUEST_BODY);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitV2Node(node);
    }

    private void visitV2Node(JsonNode node) {
        AstNodeType type = node.getType();
        if ( type.equals(OpenApi2Grammar.OPERATION) || type.equals(OpenApi3Grammar.OPERATION)) {
            List<JsonNode> responseCodes = node.get("responses").properties().stream().collect(Collectors.toList());
            for (JsonNode jsonNode : responseCodes) {
                if (!jsonNode.key().getTokenValue().equals("204")) {
                    JsonNode responseNode = jsonNode.resolve();
                    if ( responseNode.getType().equals(OpenApi3Grammar.RESPONSE) ) {
                        visitRequestBodyOrResponseV3Node(responseNode);
                    } else {
                        visitResponseV2Node(responseNode);
                    }
                }
            }
        } else if ( type.equals(OpenApi2Grammar.SCHEMA) || type.equals(OpenApi3Grammar.SCHEMA) ) {
            visitSchemaNode(node);
        }  else if ( type.equals(OpenApi3Grammar.REQUEST_BODY) ) {
            visitRequestBodyOrResponseV3Node(node);
        }
    }

    private void visitResponseV2Node(JsonNode node) {
        if (node.get("examples").isMissing()) {
            addIssue(KEY, translate("OAR031.error-response"), node.key());
        }
    }

    private void visitSchemaNode(JsonNode node) {
        JsonNode propertiesNode = getProperties(node);
        Collection<JsonNode> properties = propertiesNode.properties();
        for (JsonNode property : properties) {
            property = property.value();
            JsonNode type = getType(property);
            if (isObjectType(type) || type.isMissing()) {
                visitSchemaNode(property);
            } else if (isArrayType(type)) {
                JsonNode items = property.get("items");
                visitSchemaNode(items);
            } else {
                if (property.get("example").isMissing()) {
                    addIssue(KEY, translate("OAR031.error-property"), property.key());
                }
            }
        }
    }

    private void visitRequestBodyOrResponseV3Node(JsonNode node) {
        JsonNode content = node.at("/content");
        if (content.isMissing()) return;
        for (JsonNode mediaTypeNode : content.propertyMap().values()) {
            if (mediaTypeNode.get("examples").isMissing() && mediaTypeNode.get("example").isMissing()) {
                AstNodeType type = node.getType();
                if ( type.equals(OpenApi3Grammar.REQUEST_BODY) ) {
                    addIssue(KEY, translate("OAR031.error-request"), node.key());
                } else {
                    addIssue(KEY, translate("OAR031.error-response"), node.key());
                }
            }
        }
    }
}
