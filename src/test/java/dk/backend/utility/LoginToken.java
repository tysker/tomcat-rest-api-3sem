package dk.backend.utility;

import static io.restassured.RestAssured.given;

public class LoginToken {
    public static LoginToken instance;
    public static LoginToken getInstance() {
        if (instance == null) {
            instance = new LoginToken();
        }
        return instance;
    }

    private Object login(String username, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", username, password);

        var token =  given()
                .contentType("application/json")
                .body(json)
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .response()
                .body()
                .path("token");

        return "Bearer " + token;
    }

    public Object getAdminToken() {
        return login("admintest", "admin123");
    }

    public Object getUserToken() {
        return login("usertest", "user123");
    }
}
