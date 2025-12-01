package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.entities.Beer;
import io.javalin.http.Context;
import io.javalin.Javalin;
/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerController {

    private final BeerDAO dao = new BeerDAO();

    public void getAll(Context ctx) {
        ctx.json(dao.findAll());
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Beer beer = dao.findById(id);
        if (beer == null) {
            ctx.status(404).result("Beer not found");
        } else {
            ctx.json(beer);
        }
    }

    public void create(Context ctx) {
        Beer beer = ctx.bodyAsClass(Beer.class);
        ctx.json(dao.create(beer));
        ctx.status(201);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Beer updated = ctx.bodyAsClass(Beer.class);
        updated.setId(id);
        ctx.json(dao.update(updated));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}
