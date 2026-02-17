package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR105ResourcesByPutVerbCheck.KEY)
public class OAR105ResourcesByPutVerbCheck extends AbstractResourcesByVerbCheck {

    public static final String KEY = "OAR105";
    private static final String MESSAGE = "OAR105.error";
    private static final String RESERVED_WORDS = "get,delete";
    private static final String PUT_VERB = "put";

    public OAR105ResourcesByPutVerbCheck() {
        super(KEY, MESSAGE, PUT_VERB, RESERVED_WORDS, false);
    }
}