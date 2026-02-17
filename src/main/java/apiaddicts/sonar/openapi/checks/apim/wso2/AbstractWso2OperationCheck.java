package apiaddicts.sonar.openapi.checks.apim.wso2;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

public abstract class AbstractWso2OperationCheck extends BaseCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.OPERATION,
                OpenApi3Grammar.OPERATION
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        visitOperationNode(node);
    }

    protected abstract void visitOperationNode(JsonNode node);
}