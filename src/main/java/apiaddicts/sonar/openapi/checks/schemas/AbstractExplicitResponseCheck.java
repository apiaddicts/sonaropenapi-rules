package apiaddicts.sonar.openapi.checks.schemas;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.*;

public abstract class AbstractExplicitResponseCheck extends AbstractSchemaCheck {

    private String key;
    protected final String responseCode;

    protected AbstractExplicitResponseCheck(String key, String responseCode) {
        super(key);
        this.key = key;
        this.responseCode = responseCode;
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.RESPONSES, OpenApi3Grammar.RESPONSES, OpenApi31Grammar.RESPONSES);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitResponsesNode(node);
    }

    protected void visitResponsesNode(JsonNode node) {
        for (JsonNode responseNode : node.propertyMap().values()) {
            if (!responseCode.equals(responseNode.key().getTokenValue())) {
                continue;
            }

            JsonNode resolved = resolve(responseNode);
            AstNodeType type = resolved.getType();

            if (OpenApi2Grammar.RESPONSE.equals(type)) {
                visitV2ExplicitNode(resolved);
            }

            else if (OpenApi3Grammar.RESPONSE.equals(type) || OpenApi31Grammar.RESPONSE.equals(type)) {
                handleV3Response(resolved);
            }
        }
    }

    private void handleV3Response(JsonNode responseNode) {
        JsonNode content = responseNode.at("/content");
        if (content.isMissing()) {
            if ("OAR038".equals(key)) {
                visitV2ExplicitNode(responseNode);
            }
            return;
        }
        for (JsonNode mediaTypeNode : content.propertyMap().values()) {
            visitV2ExplicitNode(mediaTypeNode);
        }
    }

    protected abstract void visitV2ExplicitNode(JsonNode node);
}
