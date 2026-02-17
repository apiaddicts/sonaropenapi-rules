package apiaddicts.sonar.openapi.checks.security;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Rule(key = OAR075StringParameterIntegrityCheck.KEY)
public class OAR075StringParameterIntegrityCheck extends AbstractTypedParameterIntegrityCheck {

    public static final String KEY = "OAR075";
    private static final String MESSAGE = "OAR075.error";
    private static final String DEFAULT = "minLength,maxLength,enum,format";

    @RuleProperty(
            key = "parameter_integrity",
            description = "String integrity checks",
            defaultValue = DEFAULT
    )
    private String integrityStr = DEFAULT;

    public OAR075StringParameterIntegrityCheck() {
        super(KEY, MESSAGE);
    }

    @Override
    protected boolean isTargetType(JsonNode typeNode) {
        return typeNode != null && !typeNode.isMissing() && "string".equals(typeNode.getTokenValue());
    }

    @Override
    protected void validateTypedNode(JsonNode node,JsonNode typeNode) {
        Set<String> checks = Arrays.stream(integrityStr.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        boolean ok = checks.stream().allMatch(k->{
            JsonNode n = node.get(k);
            return n != null && !n.isMissing();
        });

        if(!ok) addIssue(ruleKey,translate(messageKey),typeNode);
    }
}