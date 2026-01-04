package dk.dagensoel.acceptance;

import dk.dagensoel.integration.IntegrationTestBase;

import static io.restassured.RestAssured.given;
/**
 * Purpose: Base class for acceptance tests to provide common functionality.
 *
 * @Author: Anton Friis Stengaard
 */
public abstract class AcceptanceTestBase extends IntegrationTestBase {

    protected String loginAsAdmin() {
        return given()
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

    protected Long addBeer(String token, Long eventId, String name) {
        Number id =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body("""
                            {
                              "name": "%s",
                              "brewery": "Test Brewery",
                              "country": "DK",
                              "abv": 5.5,
                              "submittedBy": "Tester"
                            }
                            """.formatted(name))
                        .when()
                        .post("/api/admin/events/" + eventId + "/beers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        return id.longValue();
    }

    protected void openVoting(String token, Long eventId) {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{ \"status\": \"VOTING\" }")
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);
    }
}
