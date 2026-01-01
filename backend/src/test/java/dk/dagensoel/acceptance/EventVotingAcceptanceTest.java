package dk.dagensoel.acceptance;

import dk.dagensoel.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

/**
 * Purpose: Test the full event voting lifecycle from admin creating event. Generated with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class EventVotingAcceptanceTest extends IntegrationTestBase {

    @Test
    void adminCanRunEventAndUsersCanVoteAndSeeResults() {

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
        var event =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Acceptance Test Event",
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

        // ---------- Admin adds beers ----------
        Long beerA =
                addBeer(adminToken, eventId, "IPA");
        Long beerB =
                addBeer(adminToken, eventId, "Stout");

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

        // ---------- User votes ----------
        given()
                .header("X-Device-Id", "acceptance-device")
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

        // ---------- Results are available ----------
        given()
                .when()
                .get("/api/events/" + eventId + "/results")
                .then()
                .statusCode(200);
    }

    private Long addBeer(String token, Long eventId, String name) {
        Number id =
                given()
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .body(
                                "{\"name\":\"" + name + "\"," +
                                        "\"brewery\":\"Test Brewery\"," +
                                        "\"country\":\"DK\"," +
                                        "\"abv\":5.0," +
                                        "\"submittedBy\":\"Tester\"}"
                        )
                        .when()
                        .post("/api/admin/events/" + eventId + "/beers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");
        return id.longValue();
    }

}
