package dk.backend.rest.authentication;

import dk.backend.utility.CreateTestData;
import dk.backend.utility.LoginToken;
import dk.backend.utility.RestServer;
import dk.backend.utilty.EMF_Creator;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import jakarta.persistence.EntityManagerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthenticationResourceTest {

    private static HttpServer server;
    private static final LoginToken LOGIN_TOKEN = LoginToken.getInstance();
    private static Object adminToken;
    private static Object userToken;

    @BeforeAll
    public static void setupClass() {
        server = RestServer.startServer();
        EMF_Creator.startFacadeWithTestDb();
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactoryForTest();
        CreateTestData.createTestData(emf.createEntityManager());
        RestAssured.defaultParser = Parser.JSON;
        adminToken = LOGIN_TOKEN.getAdminToken();
        userToken =  LOGIN_TOKEN.getUserToken();
    }

    @AfterAll
    public static void tearDownClass() {
        EMF_Creator.endFacadeWithTestDb();
        server.shutdownNow();
    }

    @Test
    @DisplayName("Login test - returns token")
    void testLogin() {
        String json = String.format("{username: \"%s\", password: \"%s\"}", "usertest", "user123");

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo("usertest"));
    }

    @Test
    @DisplayName("Register test - returns token")
    void testRegister() {
        String json = String.format("{username: \"%s\", password: \"%s\", role: \"%s\"}", "register", "register123", "user");

        given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(201)
                .body("token", notNullValue())
                .body("username", equalTo("register"));
    }

    @Test
    @DisplayName("DenyAll annotation test")
    void denyAll() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/api/auth/denyall")
                .then()
                .statusCode(401)
                .body("message", equalTo("Error processing request for path 'auth/denyall': HTTP 401 Unauthorized"));
    }

    @Test
    @DisplayName("Role admin verification")
    void adminAuthorized() {
        given()
                .header("Authorization", adminToken)
                .when()
                .get("/api/auth/admin")
                .then()
                .statusCode(200)
                .body("message", equalTo("You are authenticated to access this endpoint"));
    }

    @Test
    @DisplayName("Role admin verification failed")
    void adminUnauthorized() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/api/auth/admin")
                .then()
                .statusCode(401)
                .body("message", equalTo("Error processing request for path 'auth/admin': You are not authorized to perform the requested operation"));
    }

    @Test
    @DisplayName("Role user verification")
    void userAuthorized() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/api/auth/user")
                .then()
                .statusCode(200)
                .body("message", equalTo("You are authenticated to access this endpoint"));
    }

    @Test
    @DisplayName("Role user verification failed")
    void userUnauthorized() {
        given()
                .header("Authorization", adminToken)
                .when()
                .get("/api/auth/user")
                .then()
                .statusCode(401)
                .body("message", equalTo("Error processing request for path 'auth/user': You are not authorized to perform the requested operation"));
    }

    @Test
    @DisplayName("PermitAll annotation test")
    void all() {
        given()
                .header("Authorization", userToken)
                .when()
                .get("/api/auth/all")
                .then()
                .statusCode(200)
                .body("message", equalTo("You are authenticated to access this endpoint"));
    }

}
