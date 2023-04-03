package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.*;

@Rule(key = OAR012ParameterNamingConventionSnakeCaseCheck.KEY)
public class OAR012ParameterNamingConventionSnakeCaseCheck extends AbstractNamingConventionCheck {

    public static final String KEY = "OAR012";
    private static final String MESSAGE = "OAR012.error";

    private static Set<String> nameExceptions = new HashSet<>(Arrays.asList("$init", "$start", "$limit", "$total", "$expand", "$orderby", "$select", "$exclude", "$filter"));

    private static final String NAMING_CONVENTION = SNAKE_CASE;

    @RuleProperty(
            key = "naming-convention",
            description = "Naming convention (snake_case).",
            defaultValue = NAMING_CONVENTION)
    private static String namingConvention = NAMING_CONVENTION;

    public OAR012ParameterNamingConventionSnakeCaseCheck() {
        super(KEY, MESSAGE, namingConvention, nameExceptions);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi2Grammar.SCHEMA, OpenApi3Grammar.PARAMETER, OpenApi3Grammar.SCHEMA);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi2Grammar.PARAMETER.equals(node.getType()) || OpenApi3Grammar.PARAMETER.equals(node.getType())) {
            visitParameterNode(node);
        } else {
            visitSchemaNode(node);
        }
    }

    private void visitSchemaNode(JsonNode schemaNode) {
        if (schemaNode.getType().equals(OpenApi3Grammar.REF)) schemaNode = resolve(schemaNode);

        Map<String, JsonNode> properties = schemaNode.propertyMap();
        if (properties.containsKey("properties")) {
            Map<String, JsonNode> schemaProperties = schemaNode.get("properties").propertyMap();
            for (JsonNode property : schemaProperties.values()) {
                JsonNode nameNode = property.key();
                String name = nameNode.getTokenValue();
                validateNamingConvention(name, nameNode);
            }
        }
    }

    private void visitParameterNode(JsonNode node) {
        JsonNode inNode = node.get("in");
        String in = inNode.getTokenValue();
        if (!"path".equals(in) && !"query".equals(in)) return;
        JsonNode nameNode = node.get("name");
        String name = nameNode.getTokenValue();
        validateNamingConvention(name, nameNode);
    }
}