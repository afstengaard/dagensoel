package dk.dagensoel.routes;

import dk.dagensoel.controllers.EventController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class EventRoutes {

    private final EventController controller = new EventController();

    public void register(Javalin app) {
        app.get("/api/events", controller::getAll);
        app.get("/api/events/{id}", controller::getById);
        app.post("/api/events", controller::create);
        app.put("/api/events/{id}", controller::update);
        app.delete("/api/events/{id}", controller::delete);
        app.get("/api/events/{code}", controller::getByCode);

    }
}
