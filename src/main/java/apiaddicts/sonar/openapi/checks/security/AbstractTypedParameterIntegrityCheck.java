package apiaddicts.sonar.openapi.checks.security;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import java.util.Set;

public abstract class AbstractTypedParameterIntegrityCheck extends BaseCheck {

    protected final String ruleKey;
    protected final String messageKey;

    protected AbstractTypedParameterIntegrityCheck(String ruleKey,String messageKey) {
        this.ruleKey=ruleKey;
        this.messageKey=messageKey;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
                OpenApi2Grammar.PARAMETER,
                OpenApi3Grammar.PARAMETER,
                OpenApi31Grammar.PARAMETER
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        if(node.getType() instanceof OpenApi2Grammar) validateSwaggerParameter(node);
        else validateOas3Parameter(node);
    }

    private void validateOas3Parameter(JsonNode node) {
        JsonNode schema=node.get("schema");
        if(schema==null||schema.isMissing()) return;
        JsonNode typeNode=schema.get("type");
        if(!isTargetType(typeNode)) return;
        validateTypedNode(schema,typeNode);
    }

    private void validateSwaggerParameter(JsonNode node) {
        JsonNode typeNode=node.get("type");
        if(!isTargetType(typeNode)) return;
        validateTypedNode(node,typeNode);
    }

    protected abstract boolean isTargetType(JsonNode typeNode);
    protected abstract void validateTypedNode(JsonNode container,JsonNode typeNode);
}