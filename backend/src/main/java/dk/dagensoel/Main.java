package dk.dagensoel;

import dk.dagensoel.config.HibernateConfig;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(7070);

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
