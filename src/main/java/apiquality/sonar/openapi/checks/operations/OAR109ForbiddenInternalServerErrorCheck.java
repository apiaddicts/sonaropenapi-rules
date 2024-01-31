package apiquality.sonar.openapi.checks.operations;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.stream.Collectors;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;

@Rule(key = OAR109ForbiddenInternalServerErrorCheck.KEY)
public class OAR109ForbiddenInternalServerErrorCheck extends BaseCheck {

    public static final String KEY = "OAR109";
    private static final String MESSAGE = "OAR109.error";

    @Override
    protected void visitFile(JsonNode root) {
        super.visitFile(root);
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        JsonNode responsesNode = node.get("responses");
        if (responsesNode != null && !responsesNode.isMissing() && !responsesNode.isNull()) {
            Set<String> responseCodes = responsesNode.propertyNames().stream().collect(Collectors.toSet());

            if (responseCodes.contains("500")) {
                addIssue(KEY, translate(MESSAGE), responsesNode.key());
            }
        }
    }
}