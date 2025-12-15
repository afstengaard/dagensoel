package dk.dagensoel.controllers;

import dk.dagensoel.daos.UserDAO;
import dk.dagensoel.entities.User;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class UserController {

    private final UserDAO dao = new UserDAO();

    public void getAll(Context ctx) {
        ctx.json(dao.findAll());
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User u = dao.findById(id);
        if (u == null) ctx.status(404).result("User not found");
        else ctx.json(u);
    }

    public void create(Context ctx) {
        User u = ctx.bodyAsClass(User.class);
        ctx.json(dao.create(u));
        ctx.status(201);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        User u = ctx.bodyAsClass(User.class);
        u.setId(id);
        ctx.json(dao.update(u));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}
