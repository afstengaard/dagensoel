package dk.dagensoel.config;

import io.javalin.Javalin;
import dk.dagensoel.security.JwtUtil;


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
            ctx.header(
                    "Access-Control-Allow-Headers",
                    "Content-Type,Authorization,X-Device-Id" //To keep track of devices.
            );
            ctx.header("Access-Control-Allow-Credentials", "true");

        });

        // JWT auth for admin routes
        app.before("/api/admin/*", ctx -> {
            String authHeader = ctx.header("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).result("Missing or invalid Authorization header");
                return;
            }

            String token = authHeader.substring("Bearer ".length());

            try {
                String username = JwtUtil.validateToken(token);
                ctx.attribute("username", username);
            } catch (Exception e) {
                ctx.status(401).result("Invalid or expired token");
            }
        });


        app.options("/*", ctx -> ctx.status(200));

        System.out.println("CORS allowed origin: " + frontendOrigin);

        return app;
    }
}
