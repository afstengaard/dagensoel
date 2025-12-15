package dk.dagensoel.config;

import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ApplicationConfig {

    public static Javalin initApp() {

        String frontendOrigin =
                System.getenv().getOrDefault("FRONTEND_ORIGIN", "http://localhost:5173");

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        });

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", frontendOrigin);
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            ctx.header("Access-Control-Allow-Credentials", "true");

        });

        app.options("/*", ctx -> ctx.status(200));

        System.out.println("CORS allowed origin: " + frontendOrigin);

        return app;
    }
}
