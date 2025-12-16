package dk.dagensoel.routes;

import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Routes {

    public static void register(Javalin app) {
        new SecurityRoutes().register(app);
        new AdminUserRoutes().register(app);
        new EventRoutes().register(app);
        new BeerRoutes().register(app);
        new VoteRoutes().register(app);
    }
}
