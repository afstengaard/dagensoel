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
        app.get("/api/beers", controller::getAll);
        app.get("/api/beers/{id}", controller::getById);
        app.post("/api/beers", controller::create);
        app.put("/api/beers/{id}", controller::update);
        app.delete("/api/beers/{id}", controller::delete);
    }
}