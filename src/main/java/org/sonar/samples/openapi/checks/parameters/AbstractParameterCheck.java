package org.sonar.samples.openapi.checks.parameters;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.plugins.openapi.api.v2.OpenApi2Grammar;
import org.sonar.plugins.openapi.api.v3.OpenApi3Grammar;
import org.sonar.samples.openapi.checks.BaseCheck;
import org.sonar.samples.openapi.utils.VerbPathMatcher;
import org.sonar.sslr.yaml.grammar.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sonar.samples.openapi.utils.JsonNodeUtils.isOperation;
import static org.sonar.samples.openapi.utils.JsonNodeUtils.resolve;

public abstract class AbstractParameterCheck extends BaseCheck {

	private static final String MESSAGE = "generic.parameter-required";

	protected VerbPathMatcher matcher;
	protected String verbPathPattern;
	protected String verbExclusions;
	private String key;
	private String paramName;


	public AbstractParameterCheck(String key, String paramName) {
		this.key = key;
		this.paramName = paramName;
	}

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
	}

	@Override
	protected void visitFile(JsonNode root) {
		this.matcher = new VerbPathMatcher(verbPathPattern, verbExclusions);
	}

	@Override
	public void visitNode(JsonNode node) {
		if (Arrays.asList("OAR023", "OAR024", "OAR025").contains(key)) {
			List<Integer> allResponses = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)) // operations
				.map(JsonNode::value)
				.flatMap(n -> n.properties().stream()) // responses
				.map(JsonNode::value)
				.flatMap(n -> n.properties().stream()) // response
				.filter(responseNode -> {
					String statusCode = responseNode.key().getTokenValue();
					responseNode = resolve(responseNode);
					return (responseNode.getType().equals(OpenApi2Grammar.RESPONSE) || responseNode.getType().equals(OpenApi3Grammar.RESPONSE)) && !statusCode.equalsIgnoreCase("default");
				})
				.map(responseNode -> Integer.parseInt(responseNode.key().getTokenValue()))
				.collect(Collectors.toList());

			if (allResponses.contains(206)) {
				visitPathNode(node);
			}
		} else {
			visitPathNode(node);
		}
	}

    private void visitPathNode(JsonNode node) {
		List<String> parameters = new ArrayList<String>();
        String path = node.key().getTokenValue();
		JsonNode parametersInPathNode = node.get("parameters");
        if (node.getType() == OpenApi3Grammar.PATH) {
			parameters.addAll( listParameterNames(parametersInPathNode) );
		}
        Collection<JsonNode> operationNodes = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)).collect(Collectors.toList());
        for (JsonNode operationNode : operationNodes) {
			String verb = operationNode.key().getTokenValue();
			JsonNode verbNode = node.get(verb);
			JsonNode parametersInOperationNode = verbNode.get("parameters");
            parameters.addAll( listParameterNames(parametersInOperationNode) );
			if (match(path, verb) && !parameters.contains(paramName)) {
				if (parametersInPathNode.isMissing() && parametersInOperationNode.isMissing()) {
					addIssue(key, translate(MESSAGE, paramName), verbNode.key());
				} else if (parametersInOperationNode.isMissing()) {
					addIssue(key, translate(MESSAGE, paramName), parametersInPathNode.key());
				} else {
					addIssue(key, translate(MESSAGE, paramName), parametersInOperationNode.key());
				}
			}
        }
    }

	protected boolean match(String path, String verb) {
		return matcher.matches(verb, path);
	}

    private List<String> listParameterNames(JsonNode parametersNode) {
        if (parametersNode.isMissing() || parametersNode.isNull()) return new ArrayList<String>();
        List<String> parameterNames = parametersNode.elements().stream()
				.map(parameterNode -> parameterNode.resolve().get("name").getTokenValue()).collect(Collectors.toList());
        return parameterNames;
    }
}
