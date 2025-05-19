package apiaddicts.sonar.openapi.checks.operations;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.List;
import java.util.Set;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;

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
        return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION, OpenApi31Grammar.OPERATION);
    }

    @Override
    public void visitNode(JsonNode node) {
        JsonNode responsesNode = node.get("responses");
        if (responsesNode != null && !responsesNode.isMissing() && !responsesNode.isNull()) {
            List<String> responseCodes = responsesNode.propertyNames();
            boolean contains5xxCode = responseCodes.stream()
                                                .anyMatch(code -> code.matches("5\\d\\d"));

            if (contains5xxCode) {
                addIssue(KEY, translate(MESSAGE), responsesNode.key()); 
            }
        }
    }
}