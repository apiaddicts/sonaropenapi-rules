package apiaddicts.sonar.openapi.checks.format;

import org.sonar.check.Rule;

@Rule(key = OAR110LicenseInformationCheck.KEY)
public class OAR110LicenseInformationCheck extends AbstractInfoObjectMappingCheck {

    public static final String KEY = "OAR110";
    private static final String MESSAGE = "OAR110.error";

    public OAR110LicenseInformationCheck() {
        super(KEY, MESSAGE, "license");
    }
}