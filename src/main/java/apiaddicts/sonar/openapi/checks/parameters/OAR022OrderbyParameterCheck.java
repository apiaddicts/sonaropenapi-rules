package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR022OrderbyParameterCheck.KEY)
public class OAR022OrderbyParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR022";
    private static final String MESSAGE = "OAR022.error";
    private static final String PARAM_NAME = "$orderby";

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

    public OAR022OrderbyParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
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

    @Override
    protected boolean requiresPaginatedResponse() {
        return true;
    }
}
