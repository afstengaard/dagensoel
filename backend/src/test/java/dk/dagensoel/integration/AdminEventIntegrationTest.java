package dk.dagensoel.integration;


import dk.dagensoel.Main;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Purpose: Test that only admins can create events
 *
 * @Author: Anton Friis Stengaard with help from ChatGPT
 */
class AdminEventIntegrationTest extends IntegrationTestBase {

    static String token;

    @BeforeAll
    static void setup() {
        Main.main(new String[]{});
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;

        // Login as admin
        token =
                given()
                        .contentType("application/json")
                        .body("""
                      {
                        "username": "admin",
                        "password": "password"
                      }
                      """)
                        .when()
                        .post("/api/auth/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("token");
    }

    @Test
    void adminCanCreateEvent() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("""
                  {
                    "name": "Integration Test Event",
                    "startDate": "2025-01-01"
                  }
                  """)
                .when()
                .post("/api/admin/events")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("code", notNullValue());
    }
}
