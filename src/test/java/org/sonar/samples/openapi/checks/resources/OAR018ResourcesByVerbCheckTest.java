package org.sonar.samples.openapi.checks.resources;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.samples.openapi.BaseCheckTest;

public class OAR018ResourcesByVerbCheckTest extends BaseCheckTest {

    @Before
    public void init() {
        ruleName = "OAR018";
        check = new OAR018ResourcesByVerbCheck();
        v2Path = getV2Path("resources");
        v3Path = getV3Path("resources");
    }

    @Test
    public void verifyInV2() {
        verifyV2("plain");
    }

    @Test
    public void verifyInV3() {
        verifyV3("plain");
    }

    @Override
    public void verifyRule() {
        assertRuleProperties("OAR018 - ResourcesByVerb - Resource path not encouraged", RuleType.BUG, Severity.MAJOR, tags("resources"));
    }

    @Override
    public void verifyParameters() {
        assertNumberOfParameters(1);
        assertParameterProperties("allowed-resources-paths", ";get:^\\/[^\\/{}]*$;get:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;get:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*$;post:^\\/[^\\/{}]*$;post:^\\/[^\\/{}]*\\/get$;post:^\\/[^\\/{}]*\\/delete$;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*$;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*\\/get$;post:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/[^\\/{}]*\\/delete$;put:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;put:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;delete:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;put:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;patch:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$;patch:^\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}\\/\\/[^\\/{}]*\\/\\{[^\\/{}]*\\}$", RuleParamType.STRING);
    }
}
