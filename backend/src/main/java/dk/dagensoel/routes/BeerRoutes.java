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

        // Public
        app.get("/api/events/{eventId}/beers", controller::getByEvent);
        app.get("/api/beers/search", controller::search);


        // Admin
        app.post("/api/admin/events/{eventId}/beers", controller::create);
    }
}