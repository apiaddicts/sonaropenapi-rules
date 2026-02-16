package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR102SecondPartBasePathCheck.KEY)
public class OAR102SecondPartBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR102";

    @RuleProperty(
        key = "second-part-values",
        description = "Allowed values for the second part of the path, '-' allows any",
        defaultValue = "-"
    )
    public String secondPartValuesStr = "-";

    public OAR102SecondPartBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        Set<String> secondPartValues = secondPartValuesStr.equals("-") ? null
            : Stream.of(secondPartValuesStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

        List<String> parts = splitPath(path);

        if (secondPartValues != null && parts.size() > 1 && !secondPartValues.contains(parts.get(1))) {
            addIssue(KEY, translate("OAR102.error-second-part", parts.get(1), secondPartValuesStr), node.value());
        }
    }
}