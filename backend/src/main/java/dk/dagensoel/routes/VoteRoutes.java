package dk.dagensoel.routes;

import dk.dagensoel.controllers.VoteController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteRoutes {

    private final VoteController controller = new VoteController();

    public void register(Javalin app) {
        app.get("/api/votes", controller::getAll);
        app.get("/api/votes/{id}", controller::getById);
        app.post("/api/votes", controller::create);
        app.delete("/api/votes/{id}", controller::delete);
    }
}
