package dk.dagensoel.integration;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Purpose: Test that only one active event can exist at a time
 *
 * @Author: Anton Friis Stengaard with help from ChatGPT
 */
class EventLifecycleIntegrationTest extends IntegrationTestBase {

    @Test
    void cannotCreateSecondActiveEvent() {
        // assumes admin event already created in previous test
        given()
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
                .statusCode(401); // no token
    }
}
