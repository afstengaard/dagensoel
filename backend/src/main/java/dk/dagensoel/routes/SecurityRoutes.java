package dk.dagensoel.routes;

import dk.dagensoel.controllers.AuthController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class SecurityRoutes {
    private final AuthController controller = new AuthController();

    public void register(Javalin app) {
        app.post("/api/auth/login", controller::login);
    }
}
