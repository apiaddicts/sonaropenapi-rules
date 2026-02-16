package apiaddicts.sonar.openapi.checks.operations;

import org.sonar.check.Rule;

@Rule(key = OAR065DeleteMethodCheck.KEY)
public class OAR065DeleteMethodCheck extends AbstractHttpMethodCheck {

    public static final String KEY = "OAR065";
    private static final String MESSAGE = "OAR065.error";
    private static final String MANDATORY_CODES = "200, 202, 204";

    public OAR065DeleteMethodCheck() {
        super(KEY, MESSAGE, "delete", MANDATORY_CODES);
    }
}