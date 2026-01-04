package dk.dagensoel.acceptance;


import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
/**
 * Purpose: Test that a user can browse results of previous competitions as intended in US4. Made with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class PreviousResultsAcceptanceTest extends AcceptanceTestBase {

    /**
     * US4
     * As a user, I want to browse results of previous competitions
     * so that I can plan future events.
     */
    @Test
    void userCanBrowseResultsOfPreviousCompetitions() {

        String adminToken = loginAsAdmin();

        // Create and close event WITH votes
        String eventCode =
                createVoteAndCloseEvent(adminToken, "Past Event");

        // User browses history of previous competitions
        given()
                .when()
                .get("/api/events/history")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("[0].eventId", notNullValue())
                .body("[0].beerName", notNullValue())
                .body("[0].totalPoints", greaterThan(0));
    }

    private String createVoteAndCloseEvent(String token, String name) {

        var event =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body("""
                            {
                              "name": "%s",
                              "startDate": "2024-01-01"
                            }
                            """.formatted(name))
                        .when()
                        .post("/api/admin/events")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath();

        Long eventId = event.getLong("id");
        String eventCode = event.getString("code");

        Long beerA = addBeer(token, eventId, "IPA");
        Long beerB = addBeer(token, eventId, "Stout");

        openVoting(token, eventId);

        // User votes (anonymous)
        given()
                .header("X-Device-Id", "history-device")
                .contentType("application/json")
                .body("""
                    {
                      "favoriteBeerId": %d,
                      "secondBeerId": %d
                    }
                    """.formatted(beerA, beerB))
                .when()
                .post("/api/events/" + eventCode + "/votes")
                .then()
                .statusCode(201);

        // Close event
        given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .body("{ \"status\": \"CLOSED\" }")
                .when()
                .post("/api/admin/events/" + eventId + "/status")
                .then()
                .statusCode(200);

        return eventCode;
    }

}