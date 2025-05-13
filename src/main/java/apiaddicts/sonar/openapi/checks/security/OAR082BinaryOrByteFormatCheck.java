package apiaddicts.sonar.openapi.checks.security;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

import apiaddicts.sonar.openapi.checks.BaseCheck;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Rule(key = OAR082BinaryOrByteFormatCheck.KEY)
public class OAR082BinaryOrByteFormatCheck extends BaseCheck {

    public static final String KEY = "OAR082";
    private static final String MESSAGE = "OAR082.error";
    private static final String FIELDS_TO_APPLY = "product,line,price";

    @RuleProperty(
            key = "fields-to-apply",
            description = "List of fields where the rule should be applied (separated by comma)",
            defaultValue = FIELDS_TO_APPLY
    )
    private String fieldsApply = FIELDS_TO_APPLY;

    private List<String> fieldsList = Arrays.asList(fieldsApply.split(","));

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(OpenApi2Grammar.SCHEMA, OpenApi2Grammar.PARAMETER, OpenApi3Grammar.SCHEMA, OpenApi31Grammar.SCHEMA);
    }

    @Override
    public void visitNode(JsonNode node) {
        visitV2Node(node);
    }

    private void visitV2Node(JsonNode node) {
        JsonNode propertiesNode = node.get("properties");
        
        if (propertiesNode == null || propertiesNode.isMissing()) {
            return;
        }
        
        for (String field : fieldsList) {
            JsonNode fieldNode = propertiesNode.get(field);
            
            if (fieldNode == null || fieldNode.isMissing()) {
                continue;  
            }
            
            JsonNode typeNode = fieldNode.get("type");
            String type = typeNode.isMissing() ? null : typeNode.getTokenValue();
            
            if (!"string".equals(type)) {
                continue;  
            }
            
            JsonNode formatNode = fieldNode.get("format");
            String format = formatNode.isMissing() ? null : formatNode.getTokenValue();
    
            if (format == null || (!"binary".equals(format) && !"byte".equals(format))) {
                addIssue(KEY, translate(MESSAGE), typeNode.key());
            }
        }
    }
}
