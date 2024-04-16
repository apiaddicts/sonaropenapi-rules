package apiquality.sonar.openapi.utils;

import org.apiaddicts.apitools.dosonarapi.api.v2.OpenApi2Grammar;
import org.apiaddicts.apitools.dosonarapi.api.v3.OpenApi3Grammar;
import org.apiaddicts.apitools.dosonarapi.openapi.OpenApiConfiguration;
import org.apiaddicts.apitools.dosonarapi.openapi.parser.OpenApiParser;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.JsonNode;
import org.apiaddicts.apitools.dosonarapi.sslr.yaml.grammar.YamlParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import com.sonar.sslr.api.AstNodeType;

public class JsonNodeUtils {

    private JsonNodeUtils() {
        // Utility class
    }

    public static final String PROPERTIES = "properties";
    public static final String TYPE = "type";
    public static final String REQUIRED = "required";
    public static final String TYPE_OBJECT = "object";
    public static final String TYPE_ARRAY = "array";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_INTEGER = "integer";
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_ANY = "*";
    private static String lastFetchedContent = "";

    public static JsonNode resolve(JsonNode original) {

        if (original.isRef()) {
            String ref = original.get("$ref").getTokenValue();
            if (ref.startsWith("#")) {
                System.out.println("Internal reference: " + ref);
                return original.resolve();  
            } else {
                System.out.println("External reference: " + ref);
                retriveExternalRefContent(ref);
                return resolveExternalRef(ref);
            }
        }
        return original;
    } 
    
    public static boolean isExternalRef (JsonNode original){

        if (original.isRef()) {
            String ref = original.get("$ref").getTokenValue();
            if (ref.startsWith("#")) {
                return false;  // Resolve internal references normally
            } else {
                return true;
            }
        }
        return false;
    }

    // TODO sacar el charset de la respuesta que me da la url (contentype logs)
    // TODO Gestion de errores si la url no funciona
    private static JsonNode resolveExternalRef(String url){

        String content= retriveExternalRefContent(url);

        OpenApiConfiguration configuration = new OpenApiConfiguration(StandardCharsets.UTF_8, true);

        YamlParser parser= OpenApiParser.createGeneric(configuration);
                
        JsonNode rootNode = parser.parse(content);

        return rootNode;

    }

    private static String retriveExternalRefContent(String ref) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(ref);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            conn.connect();
    
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream()) {
                    String httpResponse = readInputStreamToString(is);
                    lastFetchedContent = httpResponse;
                    System.out.println("External JSON Response: " + httpResponse);
                    return httpResponse;
                }
            } else {
                handleErrorResponse(conn);
                return "Error: Response code " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    
    private static String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        return result.toString();
    }

    private static void handleErrorResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream errorStream = conn.getErrorStream();
        String errorResponse = errorStream != null ? readInputStreamToString(errorStream) : "No error message";
        System.out.println("Failed to fetch external JSON: HTTP error code " + responseCode);
        System.out.println("Error response: " + errorResponse);
        conn.getHeaderFields().forEach((key, value) -> System.out.println(key + ": " + value));
    }

    // Método para acceder al contenido de la última respuesta
    public static String getLastFetchedContent() {
        return lastFetchedContent;
    }

    public static JsonNode getType(JsonNode schema) {
        return schema.get(TYPE);
    }

    public static JsonNode getProperties(JsonNode schema) {
        return schema.get(PROPERTIES);
    }

    private static JsonNode parseJsonToNode(String json) {
        // Implementación del análisis de JSON a JsonNode, ajusta según la librería que uses
        // Ejemplo con Jackson (debes tener Jackson en tu proyecto):
        /*
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        */
        return null; // Cambia esto con tu lógica de conversión real
    }

    public static JsonNode getRequired(JsonNode schema) {
        return schema.get(REQUIRED);
    }

    public static Set<String> getRequiredValues(JsonNode required) {
        return required.elements().stream().map(JsonNode::getTokenValue).collect(Collectors.toSet());
    }

    public static boolean isObjectType(JsonNode schemaNode) {
        return isType(schemaNode, TYPE_OBJECT);
    }

    public static boolean isArrayType(JsonNode schemaNode) {
        return isType(schemaNode, TYPE_ARRAY);
    }

    public static boolean isStringType(JsonNode schemaNode) {
        return isType(schemaNode, TYPE_STRING);
    }

    public static boolean isIntegerType(JsonNode schemaNode) {
        return isType(schemaNode, TYPE_INTEGER);
    }

    public static boolean isBooleanType(JsonNode schemaNode) {
        return isType(schemaNode, TYPE_BOOLEAN);
    }

    public static boolean isType(JsonNode type, String name) {
        return TYPE_ANY.equals(name) || name.equals(type.getTokenValue());
    }

    public static boolean isOperation(JsonNode node) {
        AstNodeType type = node.getType();
        return type.equals(OpenApi2Grammar.OPERATION) || type.equals(OpenApi3Grammar.OPERATION);
    }
}
