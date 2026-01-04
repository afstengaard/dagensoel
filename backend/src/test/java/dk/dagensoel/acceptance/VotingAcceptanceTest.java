package dk.dagensoel.acceptance;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Purpose: Test that voting works as intended in US1 and US2. Made with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class VotingAcceptanceTest extends AcceptanceTestBase {

    /**
     * US1 + US2
     * As a user, I want to vote anonymously and easily
     * so that my vote is private and quick to submit.
     */
    @Test
    void userCanVoteAnonymouslyInActiveEvent() {

        String adminToken = loginAsAdmin();

        var event =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Voting Event",
                                  "startDate": "2025-01-01"
                                }
                                """)
                        .when()
                        .post("/api/admin/events")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath();

        Long eventId = event.getLong("id");
        String eventCode = event.getString("code");

        Long beerA = addBeer(adminToken, eventId, "IPA");
        Long beerB = addBeer(adminToken, eventId, "Stout");

        openVoting(adminToken, eventId);

        given()
                .header("X-Device-Id", "device-1")
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
    }

    @Test
    void userCannotVoteTwiceFromSameDevice() {

        String adminToken = loginAsAdmin();

        var event =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Duplicate Vote Event",
                                  "startDate": "2025-01-01"
                                }
                                """)
                        .when()
                        .post("/api/admin/events")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath();

        Long eventId = event.getLong("id");
        String eventCode = event.getString("code");

        Long beerA = addBeer(adminToken, eventId, "IPA");
        Long beerB = addBeer(adminToken, eventId, "Stout");

        openVoting(adminToken, eventId);

        given()
                .header("X-Device-Id", "device-duplicate")
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

        given()
                .header("X-Device-Id", "device-duplicate")
                .contentType("application/json")
                .body("""
                        {
                          "favoriteBeerId": %d,
                          "secondBeerId": %d
                        }
                        """.formatted(beerB, beerA))
                .when()
                .post("/api/events/" + eventCode + "/votes")
                .then()
                .statusCode(409);
    }

    @Test
    void userCannotGiveTwoVotesToSameBeer() {

        String adminToken = loginAsAdmin();

        var event =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Invalid Vote Event",
                                  "startDate": "2025-01-01"
                                }
                                """)
                        .when()
                        .post("/api/admin/events")
                        .then()
                        .statusCode(201)
                        .extract()
                        .jsonPath();

        Long eventId = event.getLong("id");
        String eventCode = event.getString("code");

        Long beer = addBeer(adminToken, eventId, "IPA");

        openVoting(adminToken, eventId);

        given()
                .header("X-Device-Id", "device-invalid")
                .contentType("application/json")
                .body("""
                        {
                          "favoriteBeerId": %d,
                          "secondBeerId": %d
                        }
                        """.formatted(beer, beer))
                .when()
                .post("/api/events/" + eventCode + "/votes")
                .then()
                .statusCode(400);
    }
}