package apiaddicts.sonar.openapi.utils;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import java.util.function.Consumer;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isExternalRef;
import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.resolve;

public class MediaTypeUtils {

    private MediaTypeUtils() {
    }

    public static void handleProducesResponses(JsonNode node, JsonNode externalRefNode, Consumer<JsonNode> visitContentNode) {
        for (JsonNode jsonNode : node.properties()) {
            if (jsonNode.key().getTokenValue().equals("204")) continue;

            boolean manageExternal = isExternalRef(jsonNode) && externalRefNode == null;
            if (manageExternal) externalRefNode = jsonNode;

            JsonNode resolved = resolve(jsonNode);
            visitContentNode.accept(resolved);

            if (manageExternal) externalRefNode = null;
        }
    }

    public static JsonNode getContentNode(JsonNode node, String section) {
        if ("consumes".equals(section)) {
            JsonNode requestBodyNode = node.at("/requestBody");
            return resolve(requestBodyNode).at("/content");
        } else {
            return node.at("/content");
        }
    }
}
