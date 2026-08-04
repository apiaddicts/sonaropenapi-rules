package apiaddicts.sonar.openapi.checks.operations;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Set;
import java.util.stream.Stream;

public abstract class AbstractResourceLevelCheck extends BaseCheck {

	private String key;

	protected AbstractResourceLevelCheck(String key) {
		this.key = key;
	}

	@Override
	public Set<AstNodeType> subscribedKinds() {
		return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
	}

	@Override
	public void visitNode(JsonNode node) {
		String path = node.key().getTokenValue();
		if (matchLevel(path)) addIssue(key, translate(messageKey(), messageArgs()), node.key());
	}

	protected abstract String messageKey();

	protected abstract Object[] messageArgs();

	private boolean matchLevel(String path) {
		long literalCount = Stream.of(path.split("/"))
				.filter(s -> !s.trim().isEmpty())
				.filter(s -> !(s.startsWith("{") && s.endsWith("}")))
				.filter(s -> !s.equals("me"))
				.count();
		return matchLevel(literalCount);
	}

	abstract boolean matchLevel(long level);
}
