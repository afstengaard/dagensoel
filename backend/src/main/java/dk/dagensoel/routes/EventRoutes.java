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

        // Public (read-only)
        app.get("/api/events/active", controller::getActive);
        app.get("/api/events/history", controller::getHistory);
        app.get("/api/events/{code}", controller::getByCode);

        // Admin (JWT-protected)
        app.post("/api/admin/events/{id}/status", controller::updateStatus);
        app.get("/api/admin/events/{id}/results", controller::getResults);
        app.post("/api/admin/events", controller::create);
    }
}
