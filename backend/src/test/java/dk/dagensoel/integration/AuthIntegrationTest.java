package dk.dagensoel.integration;

import dk.dagensoel.Main;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
/**
 * Purpose: Test authentication with invalid credentials
 *
 * @Author: Anton Friis Stengaard with help from ChatGPT
 */
class AuthIntegrationTest extends IntegrationTestBase {

    @Test
    void loginFailsWithInvalidCredentials() {
        given()
                .contentType("application/json")
                .body("""
                      {
                        "username": "nope",
                        "password": "wrong"
                      }
                      """)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }
}
