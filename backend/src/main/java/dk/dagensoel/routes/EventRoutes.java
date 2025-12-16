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

        // Public
        app.get("/api/events/code/{code}", controller::getByCode);
        app.get("/api/events/active", controller::getActive);
        app.get("/api/events/history", controller::getHistory);
        // Admin
        app.post("/api/events", controller::create);
        app.post("/api/events/{id}/status", controller::updateStatus);
        app.get("/api/events/{id}/results", controller::getResults); // next step
    }
}
