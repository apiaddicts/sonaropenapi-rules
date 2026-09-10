package apiaddicts.sonar.openapi.checks.format;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v32.OpenApi32Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

@Rule(key = OAR116PathPatternCheck.KEY)
public class OAR116PathPatternCheck extends BaseCheck {

    public static final String KEY = "OAR116";
    private static final String MESSAGE = "OAR116.error";
    private static final String DEFAULT_PATTERN = "^/";

    @RuleProperty(
            key = "pattern",
            description = "Regular expression every API path must match.",
            defaultValue = DEFAULT_PATTERN
    )
    private String patternStr = DEFAULT_PATTERN;

    private Pattern pattern;

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH, OpenApi31Grammar.PATH, OpenApi32Grammar.PATH);
    }

    @Override
    protected void visitFile(JsonNode root) {
        pattern = Pattern.compile(patternStr != null ? patternStr : DEFAULT_PATTERN);
        super.visitFile(root);
    }

    @Override
    public void visitNode(JsonNode node) {
        String path = node.key().getTokenValue();
        if (!pattern.matcher(path).find()) {
            addIssue(KEY, translate(MESSAGE, patternStr), node.key());
        }
    }
}
