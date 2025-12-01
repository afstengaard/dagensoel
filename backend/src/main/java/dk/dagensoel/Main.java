package dk.dagensoel;

import dk.dagensoel.config.HibernateConfig;
import dk.dagensoel.routes.Routes;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Main {
    public static void main(String[] args) {

        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(port);

        // Register all API routes
        Routes.register(app);

        // Simple DB test endpoint
        app.get("/test-db", ctx -> {
            try {
                var em = HibernateConfig.getEntityManagerFactory().createEntityManager();
                em.createNativeQuery("SELECT 1").getSingleResult();
                ctx.result("Hibernate connected successfully!");
            } catch (Exception e) {
                ctx.status(500).result("DB error: " + e.getMessage());
            }
        });
    }
}
