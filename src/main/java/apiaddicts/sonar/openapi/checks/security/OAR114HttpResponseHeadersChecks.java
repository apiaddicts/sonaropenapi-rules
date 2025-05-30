package apiaddicts.sonar.openapi.checks.security;

import apiaddicts.sonar.openapi.checks.BaseCheck;
import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

@Rule(key = OAR114HttpResponseHeadersChecks.KEY)
public class OAR114HttpResponseHeadersChecks extends BaseCheck{

    public static final String KEY = "OAR114";
    private static final String MANDATORY_HEADERS = "x-api-key";
    private static final String ALLOWED_HEADERS = "x-api-key, traceId, dateTime";
    // private static final String FORBIDDEN_HEADERS = "Accept, Content-Type, Authorization";

    @RuleProperty(
            key = "mandatory-headers",
            description = "List of mandatory headers. Comma separated",
            defaultValue = MANDATORY_HEADERS
    )
    private String mandatoryHeadersStr = MANDATORY_HEADERS;

    @RuleProperty(
            key = "allowed-headers",
            description = "List of allowed headers. Comma separated",
            defaultValue = ALLOWED_HEADERS
    )
    private String allowedHeadersStr = ALLOWED_HEADERS;

    // @RuleProperty(
    //         key = "forbidden-headers",
    //         description = "List of forbidden headers. Comma separated",
    //         defaultValue = FORBIDDEN_HEADERS
    // )
    // private String forbiddenHeadersStr = FORBIDDEN_HEADERS;

    
    private Set<String> mandatoryHeaders = new HashSet<>();
    private Set<String> allowedHeaders = new HashSet<>();
    // private Set<String> forbiddenHeaders = new HashSet<>();

    @Override
    protected void visitFile(JsonNode root) {
        if (!mandatoryHeadersStr.trim().isEmpty()) mandatoryHeaders.addAll(Stream.of(mandatoryHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        if (!allowedHeadersStr.trim().isEmpty()) allowedHeaders.addAll(Stream.of(allowedHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
        // if (!forbiddenHeadersStr.trim().isEmpty()) forbiddenHeaders.addAll(Stream.of(forbiddenHeadersStr.split(",")).map(header -> header.toLowerCase().trim()).collect(Collectors.toSet()));
    }

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.RESPONSE, OpenApi3Grammar.RESPONSE, OpenApi31Grammar.RESPONSE);
    }

    @Override
    public void visitNode(JsonNode node) {
      // if (node.getType() == OpenApi2Grammar.RESPONSE ||
      //   node.getType() == OpenApi3Grammar.RESPONSE ||
      //   node.getType() == OpenApi31Grammar.RESPONSE) {
      //   validateResponseHeaders(node);
      // }

        validateResponseHeaders(node);

    }

    // private void visitPathV2Node(JsonNode node) {
    //     Collection<JsonNode> operationNodes = node.properties().stream().filter(propertyNode -> isOperation(propertyNode)).collect(Collectors.toList());
    //     for (JsonNode operationNode : operationNodes) {
    //         JsonNode parametersNode = operationNode.get("parameters");
    //         List<String> headerNames = listHeaderParameters(parametersNode);
    //         if (mandatoryHeaders == null || mandatoryHeaders.isEmpty()) return;
    //         if (!headerNames.containsAll(mandatoryHeaders)) {
    //             addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), operationNode.key());
    //         }
    //     }
    // }

    private void validateResponseHeaders(JsonNode node) {
      JsonNode headersNode = node.get("headers");

      if (headersNode == null || headersNode.isMissing() || headersNode.isNull()) return;

      List<JsonNode> headerDefinitions = new ArrayList<>(headersNode.properties());
      List<String> headerNames = new ArrayList<>();

      for (JsonNode headerDef : headerDefinitions) {
          String headerName = headerDef.key().getTokenValue().toLowerCase().trim();
          headerNames.add(headerName);

          // if (forbiddenHeaders.contains(headerName)) {
          //     addIssue(KEY, translate("generic.forbidden-response-header", headerName), headerDef.key());
          // }

          if (!allowedHeaders.isEmpty() && !allowedHeaders.contains(headerName)) {
              addIssue(KEY, translate("generic.not-allowed-header", headerName), headerDef.key());
          }
      }
      if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() &&
          !headerNames.containsAll(mandatoryHeaders)) {
          addIssue(KEY, translate("generic.mandatory-headers", mandatoryHeadersStr), node.key());
      }
    }

    // private List<String> listHeaderParameters(JsonNode parametersNode) {
    //     if (parametersNode.isMissing() || parametersNode.isNull()) return new ArrayList<String>();
    //     List<JsonNode> headerParametersNodes = parametersNode.elements().stream().filter(this::isHeaderParam).collect(Collectors.toList());
    //     if (headerParametersNodes.isEmpty()) return new ArrayList<String>();
    //     for (JsonNode headerParameterNode : headerParametersNodes) {
    //         JsonNode headerNameNode = headerParameterNode.resolve().get("name");
    //         String headerName = headerNameNode.getTokenValue().toLowerCase().trim();
    //         if (!allowedHeaders.contains(headerName) || forbiddenHeaders.contains(headerName)) {
    //             addIssue(KEY, translate("generic.not-allowed-header"), headerNameNode.value());
    //         }
    //         JsonNode requiredProperty = headerParameterNode.resolve().get("required");
    //         if (mandatoryHeaders != null && !mandatoryHeaders.isEmpty() && mandatoryHeaders.contains(headerName)
    //             && ( requiredProperty.isMissing() || requiredProperty.isNull() || !Boolean.parseBoolean(requiredProperty.getTokenValue()) )
    //         ) {
    //             addIssue(KEY, translate("OAR033.error-header-required", headerParameterNode.resolve().get("name").getTokenValue()), headerNameNode.value());
    //         }
    //     }
    //     return headerParametersNodes.stream().map(headerNode -> headerNode.resolve().get("name").getTokenValue().toLowerCase().trim()).collect(Collectors.toList());
    // }

    // private boolean isHeaderParam(JsonNode n) {
    //     return n.resolve().at("/in").getTokenValue().equals("header");
    // }
}
