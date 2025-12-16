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
        app.post("/api/events/{code}/votes", controller::create);
    }
}