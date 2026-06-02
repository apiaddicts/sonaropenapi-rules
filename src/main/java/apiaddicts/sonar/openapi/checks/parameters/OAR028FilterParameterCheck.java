package apiaddicts.sonar.openapi.checks.parameters;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR028FilterParameterCheck.KEY)
public class OAR028FilterParameterCheck extends AbstractCollectionQueryParameterCheck {

    public static final String KEY = "OAR028";
    private static final String MESSAGE = "OAR028.error";
    private static final String DEFAULT_PARAM_NAME = "$filter";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the query parameter to be checked",
        defaultValue = DEFAULT_PARAM_NAME
    )
    private String filterParamName = DEFAULT_PARAM_NAME;

    public OAR028FilterParameterCheck() {
        super(KEY, MESSAGE, DEFAULT_PARAM_NAME, false);
    }

    @Override
    protected void visitFile(JsonNode root) {
        this.parameterName = filterParamName;
        super.visitFile(root);
    }
}
