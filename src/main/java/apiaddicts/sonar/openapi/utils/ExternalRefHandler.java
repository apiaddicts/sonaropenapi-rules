package apiaddicts.sonar.openapi.utils;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import static apiaddicts.sonar.openapi.utils.JsonNodeUtils.isExternalRef;

public final class ExternalRefHandler {

    private JsonNode externalRefNode;

    public void resolve(JsonNode node, Consumer<JsonNode> action) {

        boolean setExternal = false;

        if (isExternalRef(node) && externalRefNode == null) {
            externalRefNode = node;
            setExternal = true;
        }

        try {
            action.accept(JsonNodeUtils.resolve(node));
        } finally {
            if (setExternal) externalRefNode = null;
        }
    }

    public <T> T resolve(JsonNode node, Function<JsonNode, T> action) {

        boolean setExternal = false;

        if (isExternalRef(node) && externalRefNode == null) {
            externalRefNode = node;
            setExternal = true;
        }

        try {
            return action.apply(JsonNodeUtils.resolve(node));
        } finally {
            if (setExternal) externalRefNode = null;
        }
    }

    public JsonNode getTrueNode(JsonNode node) {
        return externalRefNode == null ? node : externalRefNode;
    }
}
