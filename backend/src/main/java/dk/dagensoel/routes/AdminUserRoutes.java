package dk.dagensoel.routes;

import dk.dagensoel.controllers.AdminUserController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AdminUserRoutes {

    private final AdminUserController controller = new AdminUserController();

    public void register(Javalin app) {
        //app.post("/api/admin/users", controller::create); // Not necessary with AdminBootstrap
        app.get("/api/admin/me", controller::me);
    }
}
