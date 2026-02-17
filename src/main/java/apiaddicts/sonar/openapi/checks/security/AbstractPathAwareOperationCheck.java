package apiaddicts.sonar.openapi.checks.security;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

public abstract class AbstractPathAwareOperationCheck extends BaseCheck {

    protected String currentPath;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
            OpenApi2Grammar.PATH,
            OpenApi3Grammar.PATH,
            OpenApi31Grammar.PATH,
            OpenApi2Grammar.OPERATION,
            OpenApi3Grammar.OPERATION,
            OpenApi31Grammar.OPERATION
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        AstNodeType type = node.getType();

        if (type == OpenApi2Grammar.PATH
            || type == OpenApi3Grammar.PATH
            || type == OpenApi31Grammar.PATH) {
            currentPath = node.key().getTokenValue();
            return;
        }

        handleOperation(node, type);
    }

    protected abstract void handleOperation(JsonNode node, AstNodeType type);
}