package apiaddicts.sonar.openapi.checks.parameters;

import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR019SelectParameterCheck.KEY)
public class OAR019SelectParameterCheck extends AbstractQueryParameterCheck {

    public static final String KEY = "OAR019";
    private static final String MESSAGE = "OAR019.error";
    private static final String PARAM_NAME = "$select";
    private static final String DEFAULT_PATHS = "\\/me(\\/|$),status|health|ping";
    private static final String PATH_STRATEGY = "/exclude";

    @RuleProperty(
        key = "parameterName",
        description = "Name of the parameter to be checked",
        defaultValue = PARAM_NAME
    )
    private String parameterNameOverride = PARAM_NAME;

    @RuleProperty(
        key = "paths",
        description = "List of explicit paths to include/exclude from this rule separated by comma",
        defaultValue = DEFAULT_PATHS
    )
    private String pathsOverride = DEFAULT_PATHS;

    @RuleProperty(
        key = "pathValidationStrategy",
        description = "Path validation strategy (include/exclude)",
        defaultValue = PATH_STRATEGY
    )
    private String pathCheckStrategyOverride = PATH_STRATEGY;

    public OAR019SelectParameterCheck() {
        super(
            KEY,
            MESSAGE,
            PARAM_NAME,
            false
        );
    }

    @Override
    protected String getParameterName() {
        return parameterNameOverride;
    }

    @Override
    protected String getPathsStr() {
        return pathsOverride;
    }

    @Override
    protected String getPathCheckStrategy() {
        return pathCheckStrategyOverride;
    }

}