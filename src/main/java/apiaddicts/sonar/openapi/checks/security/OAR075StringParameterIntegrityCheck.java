package apiaddicts.sonar.openapi.checks.security;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR075StringParameterIntegrityCheck.KEY)
public class OAR075StringParameterIntegrityCheck extends BaseCheck {

    public static final String KEY = "OAR075";
    private static final String MESSAGE = "OAR075.error";
    private static final String PARAMETER_INTEGRITY = "minLength,maxLength,enum,format";

    @RuleProperty(
            key = "parameter_integrity",
            description = "String parameters integrity (minLength,maxLength,pattern,enum,format).Comma separated.",
            defaultValue = PARAMETER_INTEGRITY
    )
    private String parameterIntegrityStr = PARAMETER_INTEGRITY;

    private Set<String> parameterIntegrity = new HashSet<>();

    private Set<String> getActiveIntegrityChecks() {
        return Arrays.stream(parameterIntegrityStr.split(","))
                 .map(String::trim)
                 .collect(Collectors.toSet());
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER, OpenApi31Grammar.PARAMETER);
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() instanceof OpenApi2Grammar) {
            visitSwaggerParameterNode(node);
        } else {
            visitParameterNode(node);
        }
    }

    public void visitParameterNode(JsonNode node) {
        JsonNode schemaNode = node.get("schema");

        if (schemaNode != null) {
            JsonNode typeNode = schemaNode.get("type");

            boolean isStringType = typeNode != null && "string".equals(typeNode.getTokenValue());

            if (isStringType) {
                Set<String> checks = getActiveIntegrityChecks();
                boolean hasChecks = checks.stream()
                    .allMatch(key -> schemaNode.get(key) != null && !schemaNode.get(key).isMissing());

                if (!hasChecks) {
                    addIssue(KEY, translate(MESSAGE), typeNode);
                }
            }
        }
    }

    public void visitSwaggerParameterNode(JsonNode node) {
        JsonNode typeNode = node.get("type");

        boolean isStringType = typeNode != null && "string".equals(typeNode.getTokenValue());

        if (isStringType) {

           Set<String> checks = getActiveIntegrityChecks();
            boolean hasChecks = checks.stream()
                .allMatch(key -> node.get(key) != null && !node.get(key).isMissing());

            if (!hasChecks) {
                addIssue(KEY, translate(MESSAGE), typeNode);
            }
        }
    }
}