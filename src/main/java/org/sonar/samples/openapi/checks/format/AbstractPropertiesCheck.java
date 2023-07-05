package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

public abstract class AbstractPropertiesCheck extends BaseCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi2Grammar.PARAMETER, OpenApi3Grammar.SCHEMA);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitV2Node(node);
    }

    private void visitV2Node(JsonNode node) {
        JsonNode propertiesNode = node.get("properties");
        String properties = propertiesNode.getTokenValue();
        JsonNode typeNode = node.get("type");
        String type = typeNode.getTokenValue();
        JsonNode formatNode = node.get("format");
        String format = formatNode.isMissing() ? null : formatNode.getTokenValue();
        validate(type, format, properties, typeNode, propertiesNode);
    }

    public abstract void validate(String type, String format, String properties, JsonNode typeNode, JsonNode propertiesNode);
}
