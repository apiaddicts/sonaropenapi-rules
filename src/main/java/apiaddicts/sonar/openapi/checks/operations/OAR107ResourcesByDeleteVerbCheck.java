package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR107ResourcesByDeleteVerbCheck.KEY)
public class OAR107ResourcesByDeleteVerbCheck extends AbstractResourcesByVerbCheck {

    public static final String KEY = "OAR107";
    private static final String MESSAGE = "OAR107.error";
    private static final String RESERVED_WORDS = "get,delete";
    private static final String DELETE_VERB = "delete";

    public OAR107ResourcesByDeleteVerbCheck() {
        super(KEY, MESSAGE, DELETE_VERB, RESERVED_WORDS, false);
    }
}