package apiaddicts.sonar.openapi.checks.regex;

import java.util.Set;

import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v31.OpenApi31Grammar;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import apiaddicts.sonar.openapi.checks.BaseCheck;

import com.google.common.collect.ImmutableSet;
import com.sonar.sslr.api.AstNodeType;

public abstract class AbstractOpenAPIGrammarNodeCheck extends BaseCheck {

    @Override
    public Set<AstNodeType> subscribedKinds() {
        return ImmutableSet.of(
            OpenApi3Grammar.ROOT,
            OpenApi3Grammar.PATHS,
            OpenApi3Grammar.OPERATION,
            OpenApi3Grammar.INFO,
            OpenApi3Grammar.RESPONSES,
            OpenApi3Grammar.PARAMETER,
            OpenApi3Grammar.REQUEST_BODY,
            OpenApi3Grammar.SCHEMA,
            OpenApi3Grammar.SECURITY_SCHEME,
            OpenApi3Grammar.TAG
        );
    }

    @Override
    public void visitNode(JsonNode node) {
        if (OpenApi3Grammar.ROOT.equals(node.getType())) {
            checkInfoNodes(node);            
        }        
    }

    private void checkInfoNodes(JsonNode rootNode){
        JsonNode infoNode = rootNode.get("info");

        JsonNode titleNode = infoNode.get("title");
        JsonNode descriptionNode = infoNode.get("description");

        JsonNode termsOfServiceNode = infoNode.get("termsOfService");
        
        JsonNode contactNode = infoNode.get("contact");
        JsonNode contactNameNode = contactNode != null ? contactNode.get("name") : null;
        JsonNode contactUrlNode = contactNode != null ? contactNode.get("url") : null;
        JsonNode contactEmailNode = contactNode != null ? contactNode.get("email") : null;

        JsonNode licenseNode = infoNode.get("license");
        JsonNode licenseNameNode = licenseNode != null ? licenseNode.get("name") : null;
        JsonNode licenseUrlNode = licenseNode != null ? licenseNode.get("url") : null;

        JsonNode versionNode = infoNode.get("version");
    }

}