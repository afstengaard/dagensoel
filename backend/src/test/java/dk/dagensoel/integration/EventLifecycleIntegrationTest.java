package dk.dagensoel.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Purpose: Test that only one active event can exist at a time
 *
 * @Author: Anton Friis Stengaard with help from ChatGPT
 */
class EventLifecycleIntegrationTest extends IntegrationTestBase {

    static String adminToken;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 7070;

        // Login as admin
        adminToken =
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
    void cannotCreateSecondActiveEvent() {

        // Create first event
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                {
                  "name": "First Event",
                  "startDate": "2025-01-01"
                }
                """)
                .when()
                .post("/api/admin/events")
                .then()
                .statusCode(201);

        // Attempt to create second active event
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                {
                  "name": "Second Event",
                  "startDate": "2025-02-01"
                }
                """)
                .when()
                .post("/api/admin/events")
                .then()
                .statusCode(409); // Conflict (expected)
    }
}

