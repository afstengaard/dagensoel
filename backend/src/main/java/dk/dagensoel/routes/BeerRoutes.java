package dk.dagensoel.routes;

import dk.dagensoel.controllers.BeerController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerRoutes {

    private final BeerController controller = new BeerController();

    public void register(Javalin app) {
        app.get("/api/events/{eventId}/beers", controller::getByEvent);
        app.post("/api/events/{eventId}/beers", controller::create);
    }
}