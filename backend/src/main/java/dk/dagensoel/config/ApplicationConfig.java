package dk.dagensoel.config;

import io.javalin.Javalin;
import dk.dagensoel.security.JwtUtil;
import io.javalin.http.UnauthorizedResponse;


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
                throw new UnauthorizedResponse("Missing or invalid Authorization header");
            }

            String token = authHeader.substring("Bearer ".length());

            try {
                String username = JwtUtil.validateToken(token);
                ctx.attribute("username", username);
            } catch (Exception e) {
                throw new UnauthorizedResponse("Invalid or expired token");
            }
        });


        app.options("/*", ctx -> ctx.status(200));

        System.out.println("CORS allowed origin: " + frontendOrigin);

        return app;
    }
}
