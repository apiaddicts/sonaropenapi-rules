package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR111ContactInformationCheck.KEY)
public class OAR111ContactInformationCheck extends AbstractInfoObjectMappingCheck {

    public static final String KEY = "OAR111";
    private static final String MESSAGE = "OAR111.error";

    public OAR111ContactInformationCheck() {
        super(KEY, MESSAGE, "contact");
    }
}