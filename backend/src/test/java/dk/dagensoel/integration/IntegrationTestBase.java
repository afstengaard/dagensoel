package dk.dagensoel.integration;

import dk.dagensoel.Main;
import dk.dagensoel.config.HibernateConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Purpose: Base class for integration tests setting up a PostgreSQL container
 *
 * @Author: Anton Friis Stengaard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    static boolean serverStarted = false;

    @BeforeAll
    void startInfrastructure() {
        postgres.start();

        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USER", postgres.getUsername());
        System.setProperty("DB_PASS", postgres.getPassword());

        System.setProperty("ADMIN_USERNAME", "admin");
        System.setProperty("ADMIN_PASSWORD", "password");
        System.setProperty("JWT_SECRET", "test-secret-test-secret-test-secret");

        if (!serverStarted) {
            Main.main(new String[]{});
            serverStarted = true;
        }
    }

    @BeforeEach
    void cleanDatabase() {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Vote").executeUpdate();
        em.createQuery("DELETE FROM Beer").executeUpdate();
        em.createQuery("DELETE FROM Event").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

}
