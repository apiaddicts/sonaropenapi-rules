package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR106ResourcesByPatchVerbCheck.KEY)
public class OAR106ResourcesByPatchVerbCheck extends AbstractResourcesByVerbCheck {

    public static final String KEY = "OAR106";
    private static final String MESSAGE = "OAR106.error";
    private static final String RESERVED_WORDS = "get,delete";
    private static final String PATCH_VERB = "patch";

    public OAR106ResourcesByPatchVerbCheck() {
        super(KEY, MESSAGE, PATCH_VERB, RESERVED_WORDS, false);
    }
}