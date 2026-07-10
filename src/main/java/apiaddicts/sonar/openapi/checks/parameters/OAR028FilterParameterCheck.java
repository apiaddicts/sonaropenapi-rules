package apiaddicts.sonar.openapi.checks.parameters;

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

    @RuleProperty(
        key = "paths",
        description = "List of explicit paths to include/exclude from this rule separated by comma",
        defaultValue = DEFAULT_PATH
    )
    private String pathsStr = DEFAULT_PATH;

    @RuleProperty(
        key = "pathValidationStrategy",
        description = "Path validation strategy (include/exclude)",
        defaultValue = PATH_STRATEGY
    )
    private String pathCheckStrategy = PATH_STRATEGY;

    public OAR028FilterParameterCheck() {
        super(KEY, MESSAGE, DEFAULT_PARAM_NAME, false);
    }

    @Override
    protected String getParameterName() {
        return filterParamName;
    }

    @Override
    protected String getPathsStr() {
        return pathsStr;
    }

    @Override
    protected String getPathCheckStrategy() {
        return pathCheckStrategy;
    }
}
