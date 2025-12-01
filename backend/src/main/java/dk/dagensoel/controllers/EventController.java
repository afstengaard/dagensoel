package dk.dagensoel.controllers;

import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.entities.Event;
import io.javalin.http.Context;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class EventController {

    private final EventDAO dao = new EventDAO();

    public void getAll(Context ctx) {
        ctx.json(dao.findAll());
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Event e = dao.findById(id);
        if (e == null) {
            ctx.status(404).result("Event not found");
        } else {
            ctx.json(e);
        }
    }

    public void create(Context ctx) {
        Event e = ctx.bodyAsClass(Event.class);
        ctx.json(dao.create(e));
        ctx.status(201);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Event e = ctx.bodyAsClass(Event.class);
        e.setId(id);
        ctx.json(dao.update(e));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}
