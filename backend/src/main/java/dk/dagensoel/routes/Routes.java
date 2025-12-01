package dk.dagensoel.routes;

import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class Routes {

    public static void register(Javalin app) {
        new BeerRoutes().register(app);
        new EventRoutes().register(app);
        new UserRoutes().register(app);
        new VoteRoutes().register(app);
    }
}
