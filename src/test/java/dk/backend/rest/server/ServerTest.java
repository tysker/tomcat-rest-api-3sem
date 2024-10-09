package dk.backend.rest.server;

import dk.backend.utility.RestServer;
import dk.backend.utilty.EMF_Creator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

import static io.restassured.RestAssured.given;

public class ServerTest {

    private static HttpServer server;

    @BeforeAll
    public static void setupClass() {
        server = RestServer.startServer();
        EMF_Creator.startFacadeWithTestDb();
    }

    @AfterAll
    public static void tearDownClass() {
        server.shutdownNow();
    }

    @Test
    @DisplayName("Server is up")
    void testServerIsUp() {
        given()
                .when()
                .get("/api/auth")
                .then()
                .statusCode(200)
                .body("message", equalTo("Server is running!"));
    }

    @Test
    @DisplayName("Is cors enabled check")
    void corsEnabled() {
        var response = given()
                .when()
                .options("/api/auth")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        response.log().headers();
    }
}
