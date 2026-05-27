package apiaddicts.sonar.openapi;

import org.sonar.api.ExtensionPoint;
import org.sonar.api.scanner.ScannerSide;
import org.apiaddicts.apitools.dosonarapi.api.OpenApiCustomRuleRepository;
import apiaddicts.sonar.openapi.checks.RulesLists;
import org.sonarsource.api.sonarlint.SonarLintSide;

import java.util.List;

import static apiaddicts.sonar.openapi.OpenAPICustomRulesDefinition.OPENAPI_REPOSITORY_KEY;

@SonarLintSide
@ScannerSide
@ExtensionPoint
public class OpenAPICustomOpenApiRuleRepository implements OpenApiCustomRuleRepository {
    @Override
    public String repositoryKey() {
        return OPENAPI_REPOSITORY_KEY;
    }

    @Override
    public List<Class<?>> checkClasses() {
        return RulesLists.getAllChecks();
    }
}
