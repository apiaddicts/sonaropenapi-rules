package apiquality.sonar.openapi.checks.regex;


import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import apiquality.sonar.openapi.checks.BaseCheck;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Rule(key = OAR112RegexCheck.KEY)
public class OAR112RegexCheck extends BaseCheck {

    public static final String KEY = "OAR112";
    private static final String NODE = "sample";
    private static final String ERROR_MESSAGE = "sample";
    private static final String DESCRIPTION_MESSAGE = "sample";
    private static final String SEVERITY_DEFAULT = "sample";
    private static final String FUNCTION_DEFAULT = "sample";
    private static final String EXPREG_DEFAULT = "sample";

    


    @RuleProperty(
        key = "node",
        description = "Comma-separated list of nodes to search, e.g., 'INFO,OPERATION'.",
        defaultValue = NODE
    )
    private String nodes = NODE;

    @RuleProperty(
        key = "errorMessage",
        description = "Custom error message for the rule violation.",
        defaultValue = ERROR_MESSAGE
    )
    private String errorMessage = ERROR_MESSAGE;

    @RuleProperty(
        key = "descriptionMessage",
        description = "Brief description of what the rule checks.",
        defaultValue = DESCRIPTION_MESSAGE
    )
    private String descriptionMessage = DESCRIPTION_MESSAGE;

    @RuleProperty(
        key= "severityDefault",
        description= "Type of severity",
        defaultValue = SEVERITY_DEFAULT
    )
    private String severityMessage= SEVERITY_DEFAULT;

    @RuleProperty(
        key= "functionDefault",
        description = "Type of function",
        defaultValue = FUNCTION_DEFAULT
    )
    private String functionDefault= FUNCTION_DEFAULT;

    @RuleProperty(
        key= "expreg",
        description= "Exp reg for the rule",
        defaultValue = EXPREG_DEFAULT
    )
    private String expReg= EXPREG_DEFAULT;
    

    @Override
    public Set<AstNodeType> subscribedKinds() {
        Set<String> nodeSet = new HashSet<>(Arrays.asList(nodes.split(",")));
        return nodeSet.stream()
            .map(this::mapNodesToAstNodeType)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    private Set<AstNodeType> mapNodesToAstNodeType(String scope) {
        switch (scope.trim().toUpperCase()) {
            case "INFO":
                return ImmutableSet.of(OpenApi2Grammar.INFO, OpenApi3Grammar.INFO);
            case "OPERATION":
                return ImmutableSet.of(OpenApi2Grammar.OPERATION, OpenApi3Grammar.OPERATION);
            case "PATH":
                return ImmutableSet.of(OpenApi2Grammar.PATH, OpenApi3Grammar.PATH);
            case "PATHS":
                return ImmutableSet.of(OpenApi2Grammar.PATHS, OpenApi3Grammar.PATHS);
            case "RESPONSE":
            case "RESPONSES": 
                return ImmutableSet.of(OpenApi2Grammar.RESPONSES, OpenApi3Grammar.RESPONSES);
            case "SCHEMA":
                return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi3Grammar.SCHEMA);
            case "SERVER":
                return ImmutableSet.of(OpenApi3Grammar.SERVER); 
            case "TAG":
                return ImmutableSet.of(OpenApi2Grammar.TAG, OpenApi3Grammar.TAG);
            case "PARAMETER":
                return ImmutableSet.of(OpenApi2Grammar.PARAMETER, OpenApi3Grammar.PARAMETER);
            case "REQUEST_BODY": 
                return ImmutableSet.of(OpenApi3Grammar.REQUEST_BODY);
            default:
                return ImmutableSet.of();
        }
    }

    public void demostrarSeveridadConfigurada() {
        // Usando un switch case para manejar las diferentes severidades
        switch (severityMessage.toUpperCase()) {
            case "CRITICAL":
                System.out.println("Critical es la severidad elegida.");
                break;
            case "BLOCKER":
                System.out.println("Blocker es la severidad elegida.");
                break;
            case "MAJOR":
                System.out.println("Major es la severidad elegida.");
                break;
            case "MINOR":
                System.out.println("Minor es la severidad elegida.");
                break;
            default:
                System.out.println("La severidad configurada no es reconocida.");
                break;
        }
    }

    public void demostrarFuncionSeleccionada() {
        // Aquí simplemente verificamos si la función configurada es 'pattern', aunque hay más (preguntar Omar)
        if ("pattern".equals(functionDefault)) {
            System.out.println("La función 'pattern' ha sido seleccionada para la validación.");
            // Aquí iría la lógica de validación usando 'pattern', por ejemplo, aplicar una expresión regular.
        } else {
            System.out.println("Otra función ha sido seleccionada para la validación.");
        }
    }



    // Resto de la implementación...
}


