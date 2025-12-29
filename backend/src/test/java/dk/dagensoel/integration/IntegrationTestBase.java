package dk.dagensoel.integration;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Purpose: Base class for integration tests setting up a PostgreSQL container
 *
 * @Author: Anton Friis Stengaard
 */
public abstract class IntegrationTestBase {

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @BeforeAll
    static void startContainer() {
        postgres.start();

        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USER", postgres.getUsername());
        System.setProperty("DB_PASS", postgres.getPassword());

        System.setProperty("ADMIN_USERNAME", "admin");
        System.setProperty("ADMIN_PASSWORD", "password");
        System.setProperty("JWT_SECRET", "test-secret-test-secret-test-secret");
    }
}
