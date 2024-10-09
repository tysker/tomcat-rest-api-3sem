package dk.backend.utility;

import dk.backend.rest.ApplicationConfig;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class RestServer {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    static HttpServer server;

    public static HttpServer startServer() {

        if (server != null) {
            return server;
        }

        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());

        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

}
