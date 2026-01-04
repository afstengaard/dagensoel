package dk.dagensoel.acceptance;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
/**
 * Purpose: Test that a user can see an overview of beers in the active event as intended in US5. Made with help from ChatGPT.
 *
 * @Author: Anton Friis Stengaard
 */
class ActiveEventOverviewAcceptanceTest extends AcceptanceTestBase {

    /**
     * US5
     * As a user, I want an overview of the beers in the active competition
     * so that I can make an informed decision when voting.
     */
    @Test
    void userCanSeeOverviewOfBeersInActiveEvent() {

        String adminToken = loginAsAdmin();

        var event =
                given()
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .body("""
                                {
                                  "name": "Active Event",
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

        addBeer(adminToken, eventId, "IPA");
        addBeer(adminToken, eventId, "Stout");

        openVoting(adminToken, eventId);

        given()
                .when()
                .get("/api/events/" + eventCode)
                .then()
                .statusCode(200)
                .body("beers", hasSize(2))
                .body("beers.name", hasItems("IPA", "Stout"))
                .body("beers.brewery", everyItem(notNullValue()))
                .body("beers.abv", everyItem(notNullValue()))
                .body("beers.submittedBy", everyItem(notNullValue()));
    }
}
