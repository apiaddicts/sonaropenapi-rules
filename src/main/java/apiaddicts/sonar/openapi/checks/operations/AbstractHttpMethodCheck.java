package apiaddicts.sonar.openapi.checks.operations;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.RuleProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractHttpMethodCheck extends BaseCheck {

    private static final String PATH_STRATEGY_EXCLUDE = "/exclude";
    private static final String PATH_STRATEGY_INCLUDE = "/include";
    private static final String DEFAULT_PATHS = "/status, /another";
    private static final String PATH_STRATEGY = "/exclude";

    protected final String ruleKey;
    protected final String messageKey;
    protected final String httpMethod;

    private Set<String> mandatoryResponseCodes = new HashSet<>();
    private Set<String> exclusion;
    private String currentPath;

    @RuleProperty(
            key = "mandatory-response-codes",
            description = "List of allowed response codes. Or separated",
            defaultValue = ""
    )
    private String mandatoryResponseCodesStr;

    @RuleProperty(
            key = "paths",
            description = "List of explicit paths to include/exclude from this rule separated by comma",
            defaultValue = DEFAULT_PATHS
    )
    private String pathsStr = DEFAULT_PATHS;

    @RuleProperty(
            key = "pathValidationStrategy",
            description = "Path validation strategy (include/exclude)",
            defaultValue = PATH_STRATEGY
    )
    private String pathCheckStrategy = PATH_STRATEGY;

    protected AbstractHttpMethodCheck(
            String ruleKey,
            String messageKey,
            String httpMethod,
            String mandatoryCodesStr
    ) {
        this.ruleKey = ruleKey;
        this.messageKey = messageKey;
        this.httpMethod = httpMethod.toLowerCase();
        this.mandatoryResponseCodesStr = mandatoryCodesStr;
    }

    @Override
    protected void visitFile(JsonNode root) {
        if (mandatoryResponseCodesStr != null && !mandatoryResponseCodesStr.trim().isEmpty()) {
            mandatoryResponseCodes.addAll(
                    Stream.of(mandatoryResponseCodesStr.split(","))
                            .map(String::trim)
                            .collect(Collectors.toSet())
            );
        }

        if (pathsStr != null && !pathsStr.trim().isEmpty()) {
            exclusion = Arrays.stream(pathsStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        }
        super.visitFile(root);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH,
                OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        if (node.getType() == OpenApi2Grammar.PATH || node.getType() == OpenApi3Grammar.PATH || node.getType() == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
        } else if (node.getType() == OpenApi2Grammar.OPERATION || node.getType() == OpenApi3Grammar.OPERATION || node.getType() == OpenApi31Grammar.OPERATION) {
            if (shouldExcludePath()) {
                return;
            }

            String operationType = node.key().getTokenValue();
            if (!httpMethod.equalsIgnoreCase(operationType)) {
                return;
            }

            JsonNode responsesNode = node.get("responses");
            if (responsesNode.isMissing() || responsesNode.isNull()) {
                addIssue(ruleKey, translate(messageKey, String.join(", ", mandatoryResponseCodes)), responsesNode.key());
                return;
            }

            Set<String> responseCodes = responsesNode.propertyNames().stream().map(String::trim).collect(Collectors.toSet());

            boolean hasMandatoryCode = mandatoryResponseCodes.stream().anyMatch(responseCodes::contains);

            if (!hasMandatoryCode) {
                addIssue(ruleKey, translate(messageKey, String.join(", ", mandatoryResponseCodes)), responsesNode.key());
            }
        }
    }

    private boolean shouldExcludePath() {
        if (pathCheckStrategy.equals(PATH_STRATEGY_EXCLUDE)) {
            return exclusion.contains(currentPath);
        } else if (pathCheckStrategy.equals(PATH_STRATEGY_INCLUDE)) {
            return !exclusion.contains(currentPath);
        }
        return false;
    }
}