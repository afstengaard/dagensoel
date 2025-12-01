package dk.dagensoel.controllers;

import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.entities.Vote;
import io.javalin.http.Context;
import io.javalin.Javalin;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteController {

    private final VoteDAO dao = new VoteDAO();

    public void getAll(Context ctx) {
        ctx.json(dao.findAll());
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Vote v = dao.findById(id);
        if (v == null) ctx.status(404).result("Vote not found");
        else ctx.json(v);
    }

    public void create(Context ctx) {
        Vote v = ctx.bodyAsClass(Vote.class);
        ctx.json(dao.create(v));
        ctx.status(201);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}
