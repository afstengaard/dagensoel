package dk.dagensoel.controllers;

import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.dtos.EventDTO;
import dk.dagensoel.entities.Event;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class EventController {

    private final EventDAO dao = new EventDAO();

    //READ

    public void getAll(Context ctx) {
        ctx.json(
                dao.findAll()
                        .stream()
                        .map(EventDTO::new)
                        .toList()
        );
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Event event = dao.findById(id);

        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        ctx.json(new EventDTO(event));
    }

    public void getByCode(Context ctx) {

        long code;
        try {
            code = Long.parseLong(ctx.pathParam("code"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid event code");
            return;
        }

        Event event = dao.findByCode(code);

        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        ctx.json(new EventDTO(event));
    }

    //WRITE

    public void create(Context ctx) {
        Event event = ctx.bodyAsClass(Event.class);
        Event created = dao.create(event);
        ctx.status(201).json(new EventDTO(created));
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Event event = ctx.bodyAsClass(Event.class);
        event.setId(id);

        Event updated = dao.update(event);
        ctx.json(new EventDTO(updated));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}
