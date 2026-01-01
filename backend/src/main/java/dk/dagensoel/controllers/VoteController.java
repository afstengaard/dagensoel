package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.dtos.VoteDTO;
import dk.dagensoel.entities.*;
import io.javalin.http.BadRequestResponse;
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

        if (dto.favoriteBeerId == null || dto.secondBeerId == null) {
            ctx.status(400).result("Both votes must be selected");
            return;
        }

        if (dto.favoriteBeerId.equals(dto.secondBeerId)) {
            ctx.status(400).result("Favorite and second cannot be the same beer");
            return;
        }

        Event event = eventDAO.findByCode(code);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (event.getStatus() != EventStatus.VOTING) {
            ctx.status(403).result("Voting is not open");
            return;
        }

        String deviceHash = resolveDeviceHash(ctx);

        if (voteDAO.hasVoted(event, deviceHash, VoteType.FAVORITE)
                || voteDAO.hasVoted(event, deviceHash, VoteType.SECOND)) {
            ctx.status(409).result("You have already voted");
            return;
        }

        Beer favorite = beerDAO.findById(dto.favoriteBeerId);
        Beer second = beerDAO.findById(dto.secondBeerId);

        if (favorite == null || second == null
                || !favorite.getEvent().getId().equals(event.getId())
                || !second.getEvent().getId().equals(event.getId())) {
            ctx.status(400).result("Invalid beer selection");
            return;
        }
        voteDAO.createVotePair(event, favorite, second, deviceHash);
        ctx.status(201);
    }

    // DEVICE IDENTIFICATION

    private String resolveDeviceHash(Context ctx) {
        String header = ctx.header("X-Device-Id");
        if (header == null || header.isBlank()) {
            throw new BadRequestResponse("Missing X-Device-Id header");
        }
        return header;
    }

}