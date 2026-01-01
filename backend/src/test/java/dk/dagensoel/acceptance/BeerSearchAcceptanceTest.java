package dk.dagensoel.acceptance;

import dk.dagensoel.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Purpose: Test that a user can search for beers from previous events. Generated with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class BeerSearchAcceptanceTest extends IntegrationTestBase {

    @Test
    void userCanSearchForBeersFromPreviousEvents() {

        // ---------- Admin logs in ----------
        String adminToken =
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

        // ---------- Admin creates event ----------
        Number eventIdNumber =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Past Event",
                                  "startDate": "2024-01-01"
                                }
                                """)
                        .when()
                        .post("/api/admin/events")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");

        Long eventId = eventIdNumber.longValue();

        // ---------- Admin adds beers ----------
        addBeer(adminToken, eventId, "Test IPA");
        addBeer(adminToken, eventId, "Test Stout");

        // ---------- Admin opens voting ----------
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                        { "status": "VOTING" }
                        """)
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);

// ---------- Admin closes event ----------
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("""
                        { "status": "CLOSED" }
                        """)
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);


        // ---------- User searches beers ----------
        given()
                .queryParam("q", "IPA")
                .when()
                .get("/api/beers/search")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("beerName", hasItem("Test IPA"))
                .body("eventId", not(empty()));
    }

    private void addBeer(String token, Long eventId, String name) {
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
                .statusCode(201);
    }
}
