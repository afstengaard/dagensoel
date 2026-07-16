package dk.dagensoel.routes;

import dk.dagensoel.controllers.ImportController;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ImportRoutes {

    private final ImportController controller = new ImportController();

    public void register(Javalin app) {
        // Admin (JWT-protected)
        app.post("/api/admin/import/historical", controller::importHistorical);
    }
}
