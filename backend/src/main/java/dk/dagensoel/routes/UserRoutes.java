package dk.dagensoel.routes;

import dk.dagensoel.controllers.UserController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class UserRoutes {

    private final UserController controller = new UserController();

    public void register(Javalin app) {
        app.get("/api/users", controller::getAll);
        app.get("/api/users/{id}", controller::getById);
        app.post("/api/users", controller::create);
        app.put("/api/users/{id}", controller::update);
        app.delete("/api/users/{id}", controller::delete);
    }
}
