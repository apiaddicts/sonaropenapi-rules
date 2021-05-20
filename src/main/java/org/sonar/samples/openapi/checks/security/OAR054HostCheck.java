package org.sonar.samples.openapi.checks.security;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

@Rule(key = OAR054HostCheck.KEY)
public class OAR054HostCheck extends BaseCheck {

	public static final String KEY = "OAR054";
    
    private static final String HOST_REGEX = "^((\\*|[\\w\\d]+(-[\\w\\d]+)*)\\.)*(cloudappi)(\\.net)$";

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.ROOT, OpenApi3Grammar.SERVER);
	}

	@Override
	public void visitNode(JsonNode node) {
		if (node.getType() instanceof OpenApi2Grammar) {
			visitV2Node(node);
		} else {
			visitV3Node(node);
		}
	}

	private void visitV2Node(JsonNode node) {
        JsonNode hostNode = node.get("host");
		if (hostNode.isMissing() || hostNode.isNull()) {
			addIssue(KEY, translate("OAR054.host-mandatory"), node.key());
		} else {
            String host = hostNode.getTokenValue();
            validateHost(host, hostNode);
		}
	}

	private void visitV3Node(JsonNode node) {
        JsonNode urlNode = node.get("url");
        String server = urlNode.getTokenValue();
		try {
            String host = new URL(server).getHost();
            validateHost(host, urlNode);
		} catch (MalformedURLException e) {
            addIssue(KEY, translate("generic.malformed-url"), urlNode.value());
		}
    }
    
    private void validateHost(String host, JsonNode node) {
        if (!host.matches(HOST_REGEX)){
            addIssue(KEY, translate("OAR054.host-format"), node.value());
        }
    }
}
