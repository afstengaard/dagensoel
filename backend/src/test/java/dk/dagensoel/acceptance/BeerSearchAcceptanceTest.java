package dk.dagensoel.acceptance;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Purpose: Test that a user can search for beers from previous events as intended in US3. Made with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class BeerSearchAcceptanceTest extends AcceptanceTestBase {

    /**
     * US3
     * As a user, I want to browse beers from previous competitions
     * so that I do not bring a beer that has already been used.
     */
    @Test
    void userCanSearchForBeersFromPreviousEvents() {

        // Admin logs in
        String adminToken = loginAsAdmin();

        // Admin creates a past event
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

        // Admin adds beers to the event
        addBeer(adminToken, eventId, "Test IPA");
        addBeer(adminToken, eventId, "Test Stout");

        // Admin opens voting
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{ \"status\": \"VOTING\" }")
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);

        // Admin closes the event
        given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body("{ \"status\": \"CLOSED\" }")
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);

        // User searches for beers from previous events
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
}