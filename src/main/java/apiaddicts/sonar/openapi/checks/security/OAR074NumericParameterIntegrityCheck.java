package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isType;

@Rule(key = OAR074NumericParameterIntegrityCheck.KEY)
public class OAR074NumericParameterIntegrityCheck extends AbstractTypedParameterIntegrityCheck {

    public static final String KEY = "OAR074";
    private static final String MESSAGE = "OAR074.error";

    public OAR074NumericParameterIntegrityCheck() {
        super(KEY, MESSAGE);
    }

    @Override
    protected boolean isTargetType(JsonNode typeNode) {
        return isType(typeNode, "integer") || isType(typeNode, "number") || isType(typeNode, "float");
    }

    @Override
    protected void validateTypedNode(JsonNode node,JsonNode typeNode) {
        JsonNode min = node.get("minimum");
        JsonNode max = node.get("maximum");
        JsonNode format = node.get("format");

        boolean lacksPair = (min.isMissing() && !max.isMissing()) || (!min.isMissing() && max.isMissing());
        boolean formatAlone = !format.isMissing() && min.isMissing() && max.isMissing();
        boolean allMissing = min.isMissing() && max.isMissing() && format.isMissing();
        boolean invalid = lacksPair || allMissing;

        if(!formatAlone && invalid) addIssue(ruleKey,translate(messageKey),typeNode);
    }
}