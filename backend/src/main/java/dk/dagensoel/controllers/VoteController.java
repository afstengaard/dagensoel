package dk.dagensoel.controllers;

import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.dtos.VoteDTO;
import dk.dagensoel.dtos.VoteReadDTO;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.Vote;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteController {

    private final VoteDAO dao = new VoteDAO();
    private final EventDAO eventDAO = new EventDAO();

    /* -------------------- PUBLIC VOTING -------------------- */

    public void create(Context ctx) {

        long code;
        try {
            code = Long.parseLong(ctx.pathParam("code"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid event code");
            return;
        }

        VoteDTO dto = ctx.bodyAsClass(VoteDTO.class);

        Event event = eventDAO.findByCode(code);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (!event.isVotingOpen()) {
            ctx.status(403).result("Voting is not open");
            return;
        }

        if (dto.getFavoriteBeerId() == dto.getSecondFavoriteBeerId()) {
            ctx.status(400).result("You cannot vote for the same beer twice");
            return;
        }

        Beer favorite = dao.findBeerById(dto.getFavoriteBeerId());
        Beer second = dao.findBeerById(dto.getSecondFavoriteBeerId());

        if (favorite == null || second == null) {
            ctx.status(400).result("Invalid beer selection");
            return;
        }

        if (favorite.getEvent() != event || second.getEvent() != event) {
            ctx.status(400).result("Beers do not belong to this event");
            return;
        }

        dao.create(Vote.builder()
                .event(event)
                .favoriteBeer(favorite)
                .secondFavoriteBeer(second)
                .build());

        ctx.status(201); // ← no body
    }

    /* -------------------- ADMIN -------------------- */

    public void getAll(Context ctx) {
        ctx.json(
                dao.findAll()
                        .stream()
                        .map(VoteReadDTO::new)
                        .toList()
        );
    }

    public void getById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Vote vote = dao.findById(id);

        if (vote == null) {
            ctx.status(404).result("Vote not found");
            return;
        }

        ctx.json(new VoteReadDTO(vote));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        dao.delete(id);
        ctx.status(204);
    }
}