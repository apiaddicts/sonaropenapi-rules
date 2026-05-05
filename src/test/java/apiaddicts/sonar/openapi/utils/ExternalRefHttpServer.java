package apiaddicts.sonar.openapi.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public final class ExternalRefHttpServer {

    public static final int PORT = 18089;
    public static final String BASE_URL = "http://localhost:" + PORT;

    private static final Path FIXTURES = Paths.get("src", "test", "resources", "externalRef").toAbsolutePath().normalize();

    private static HttpServer server;

    private ExternalRefHttpServer() {
    }

    public static synchronized void start() {
        if (server != null) {
            return;
        }
        try {
            HttpServer s = HttpServer.create(new InetSocketAddress("127.0.0.1", PORT), 0);
            s.createContext("/", ExternalRefHttpServer::handle);
            s.setExecutor(null);
            s.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> s.stop(0)));
            server = s;
        } catch (IOException e) {
            throw new IllegalStateException("Could not start external ref HTTP server on port " + PORT, e);
        }
    }

    private static void handle(HttpExchange exchange) throws IOException {
        try {
            String name = exchange.getRequestURI().getPath().replaceFirst("^/+", "");
            Path file = FIXTURES.resolve(name).normalize();
            if (!file.startsWith(FIXTURES) || !Files.isRegularFile(file)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }
            byte[] body = Files.readAllBytes(file);
            exchange.getResponseHeaders().set("Content-Type", "application/yaml; charset=utf-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        } finally {
            exchange.close();
        }
    }
}
