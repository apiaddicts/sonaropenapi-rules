package apiquality.sonar.openapi.checks.format;

import apiquality.sonar.openapi.checks.BaseCheck;
import java.util.Set;
import java.util.stream.Collectors;

import apiquality.sonar.openapi.checks.parameters.OAR020ExpandParameterCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiquality.sonar.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.AstNodeType;

import java.util.Arrays;
import java.util.HashSet;

@Rule(key = OAR112CustomFieldCheck.KEY)
public class OAR112CustomFieldCheck extends BaseCheck {

    public static final String KEY = "OAR020";
    private static final String MESSAGE = "OAR020.error";


}
