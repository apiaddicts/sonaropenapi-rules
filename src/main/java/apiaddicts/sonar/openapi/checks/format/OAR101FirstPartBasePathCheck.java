package apiaddicts.sonar.openapi.checks.format;

import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Rule(key = OAR101FirstPartBasePathCheck.KEY)
public class OAR101FirstPartBasePathCheck extends AbstractBasePathCheck {

    public static final String KEY = "OAR101";

    @RuleProperty(
        key = "first-part-values",
        description = "Allowed values for the first part of the path, '-' allows any",
        defaultValue = "-"
    )
    public String firstPartValuesStr = "-";

    public OAR101FirstPartBasePathCheck() {
        super(KEY);
    }

    @Override
    protected void validateBasePath(String path, JsonNode node) {
        Set<String> firstPartValues = firstPartValuesStr.equals("-") ? null
            : Stream.of(firstPartValuesStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

        List<String> parts = splitPath(path);

        if (firstPartValues != null && !parts.isEmpty() && !firstPartValues.contains(parts.get(0))) {
            addIssue(KEY, translate("OAR101.error-first-part", parts.get(0), firstPartValuesStr), node.value());
        }
    }
}