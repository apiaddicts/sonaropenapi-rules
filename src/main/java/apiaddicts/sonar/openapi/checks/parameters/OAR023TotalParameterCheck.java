package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR023TotalParameterCheck.KEY)
public class OAR023TotalParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR023";
    private static final String MESSAGE = "OAR023.error";
    private static final String PARAM_NAME = "$total";

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

    public OAR023TotalParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            true
        );
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