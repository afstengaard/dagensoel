package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.dtos.VoteDTO;
import dk.dagensoel.entities.*;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteController {

    private final VoteDAO voteDAO = new VoteDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final BeerDAO beerDAO = new BeerDAO();

    public void create(Context ctx) {

        String code = ctx.pathParam("code");
        VoteDTO dto = ctx.bodyAsClass(VoteDTO.class);

        Event event = eventDAO.findByCode(code);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (event.getStatus() != EventStatus.VOTING) {
            ctx.status(403).result("Voting is not open");
            return;
        }

        Beer beer = beerDAO.findById(dto.beerId);
        if (beer == null || !beer.getEvent().equals(event)) {
            ctx.status(400).result("Invalid beer for this event");
            return;
        }

        String deviceHash = resolveDeviceHash(ctx);

        if (voteDAO.hasVoted(event, deviceHash, dto.type)) {
            ctx.status(409).result("You have already cast this vote");
            return;
        }

        Vote vote = new Vote();
        vote.setEvent(event);
        vote.setBeer(beer);
        vote.setDeviceHash(deviceHash);
        vote.setType(dto.type);
        vote.setPoints(dto.type == VoteType.FAVORITE ? 2 : 1);

        voteDAO.create(vote);

        ctx.status(201);
    }

    // DEVICE IDENTIFICATION

    private String resolveDeviceHash(Context ctx) {
        String header = ctx.header("X-Device-Id");
        if (header == null || header.isBlank()) {
            return ctx.req().getRemoteAddr(); // fallback
        }
        return header;
    }
}