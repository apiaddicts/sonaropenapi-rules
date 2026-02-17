package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR103ResourcesByGetVerbCheck.KEY)
public class OAR103ResourcesByGetVerbCheck extends AbstractResourcesByVerbCheck {

    public static final String KEY = "OAR103";
    private static final String MESSAGE = "OAR103.error";
    private static final String RESERVED_WORDS = "get,delete";
    private static final String GET_VERB = "get";

    public OAR103ResourcesByGetVerbCheck() {
        super(KEY, MESSAGE, GET_VERB, RESERVED_WORDS, false);
    }
}