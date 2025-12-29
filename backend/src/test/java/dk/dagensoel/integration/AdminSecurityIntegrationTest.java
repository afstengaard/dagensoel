package dk.dagensoel.integration;

import dk.dagensoel.Main;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
/**
 * Purpose: Test that admin endpoints are secured and require JWT
 *
 * @Author: Anton Friis Stengaard with help from ChatGPT
 */
class AdminSecurityIntegrationTest extends IntegrationTestBase {

    @BeforeAll
    static void setup() {
        Main.main(new String[]{});
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;
    }

    @Test
    void adminEndpointsRequireJwt() {
        given()
                .contentType("application/json")
                .body("""
                  {
                    "name": "Test Event",
                    "startDate": "2025-01-01"
                  }
                  """)
                .when()
                .post("/api/admin/events")
                .then()
                .statusCode(401);
    }
}
