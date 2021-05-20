package org.sonar.samples.openapi.checks.format;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR042BasePathCheck.KEY)
public class OAR042BasePathCheck extends BaseCheck {

    public static final String KEY = "OAR042";

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.SERVER);
    }

    @Override
    public void visitNode(JsonNode node) {
		if (node.getType() instanceof OpenApi2Grammar) {
			visitV2Node(node);
		} else {
			visitV3ServerNode(node);
		}
    }

    private void visitV2Node(JsonNode node) {
    	JsonNode basePathNode = node.get("basePath");
        String path = basePathNode.getTokenValue();
        validatePath(path, basePathNode);
    }

	private void visitV3ServerNode(JsonNode node) {
		JsonNode urlNode = node.get("url");
        String server = urlNode.getTokenValue();
		try {
            String path = new URL(server).getPath();
            validatePath(path, urlNode);
		} catch (MalformedURLException e) {
            addIssue(KEY, translate("generic.malformed-url"), urlNode.value());
		}
    }
    
    private void validatePath(String path, JsonNode node) {
        List<String> pathParts = Stream.of(path.split("/")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        if (pathParts.size() < 2) {
            addIssue(KEY, translate("OAR042.error-path-short"), node.value());
        } else if (pathParts.size() > 2) {
            addIssue(KEY, translate("OAR042.error-path-long"), node.value());
        }

        if ( !pathParts.isEmpty() && ( !pathParts.get(0).startsWith("api-") || pathParts.get(0).length() == 4 ) ) {
            addIssue(KEY, translate("OAR042.error-prefix"), node.value());
        }

        if ( pathParts.size() > 1 && ( !pathParts.get(1).toLowerCase().startsWith("v") || !isInteger( pathParts.get(1).substring(1) ) ) ) {
            addIssue(KEY, translate("OAR042.error-version"), node.value());
        }
    }

    private static boolean isInteger(String s) {
        try { 
            Integer.parseInt(s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

}
