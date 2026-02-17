package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR104ResourcesByPostVerbCheck.KEY)
public class OAR104ResourcesByPostVerbCheck extends AbstractResourcesByVerbCheck {

    public static final String KEY = "OAR104";
    private static final String MESSAGE = "OAR104.error";
    private static final String RESERVED_WORDS = "me,search";
    private static final String POST_VERB = "post";

    public OAR104ResourcesByPostVerbCheck() {
        super(KEY, MESSAGE, POST_VERB, RESERVED_WORDS, true);
    }
}