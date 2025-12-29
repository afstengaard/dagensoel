package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.dtos.EventDTO;
import dk.dagensoel.dtos.ResultDTO;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.EventStatus;
import io.javalin.http.Context;

import java.util.List;
import java.util.Objects;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class EventController {

    private final EventDAO eventDAO = new EventDAO();
    private final VoteDAO voteDAO = new VoteDAO();
    private final BeerDAO beerDAO = new BeerDAO();


    // READ
    public void getByCode(Context ctx) {
        String code = ctx.pathParam("code");

        Event event = eventDAO.findByCode(code);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        ctx.json(new EventDTO(event));
    }

    public void getActive(Context ctx) {
        try {
            Event event = eventDAO.findActiveEvent();
            if (event == null) {
                ctx.status(404).result("No active event");
                return;
            }
            ctx.json(new EventDTO(event));
        } catch (IllegalStateException e) {
            ctx.status(409).result(e.getMessage());
        }
    }

    // WRITE

    public void create(Context ctx) {
        if (eventDAO.hasActiveEvent()) {
            ctx.status(409).result("An active event already exists");
            return;
        }
        EventDTO dto = ctx.bodyAsClass(EventDTO.class);

        Event event = new Event();
        event.setName(dto.name);
        event.setStatus(EventStatus.OPEN);
        event.setCode(eventDAO.generateUniqueCode());
        event.setStartDate(dto.startDate);

        Event created = eventDAO.create(event);
        ctx.status(201).json(new EventDTO(created));
    }

    public void updateStatus(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        EventDTO dto = ctx.bodyAsClass(EventDTO.class);

        Event event = eventDAO.findById(id);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (!isValidTransition(event.getStatus(), dto.status)) {
            ctx.status(409).result("Invalid event status transition");
            return;
        }

        if (dto.status == EventStatus.VOTING) {
            try {
                Event active = eventDAO.findActiveEvent();
                if (active != null && !active.getId().equals(event.getId())) {
                    ctx.status(409).result("Another active event already exists");
                    return;
                }
            } catch (IllegalStateException e) {
                ctx.status(409).result(e.getMessage());
                return;
            }
        }


        event.setStatus(dto.status);
        eventDAO.update(event);

        ctx.json(new EventDTO(event, false));
    }

    // HELPERS

    private boolean isValidTransition(EventStatus current, EventStatus next) {
        return (current == EventStatus.OPEN && next == EventStatus.VOTING)
                || (current == EventStatus.VOTING && next == EventStatus.CLOSED);
    }

    public void getResults(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("id"));

        Event event = eventDAO.findById(eventId);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (event.getStatus() != EventStatus.CLOSED) {
            ctx.status(403).result("Results are not available yet");
            return;
        }

        List<ResultDTO> results =
                voteDAO.getResultsForEvent(eventId)
                        .stream()
                        .map(row -> {
                            Long beerId = ((Number) row[0]).longValue();
                            String beerName = (String) row[1];
                            int totalPoints = ((Number) row[2]).intValue();

                            Beer beer = beerDAO.findById(beerId);

                            return new ResultDTO(
                                    event.getId(),
                                    event.getStartDate(),
                                    beerId,
                                    beerName,
                                    beer.getSubmittedBy(),
                                    totalPoints
                            );
                        })
                        .toList();

        ctx.json(results);
    }


    public void getHistory(Context ctx) {

        List<Event> closedEvents = eventDAO.findClosedEvents();

        List<ResultDTO> history = closedEvents.stream()
                .map(event -> {

                    List<Object[]> results =
                            voteDAO.getResultsForEvent(event.getId());

                    if (results.isEmpty()) {
                        return null;
                    }

                    Object[] winner = results.get(0);

                    Long beerId = ((Number) winner[0]).longValue();
                    String beerName = (String) winner[1];
                    int totalPoints = ((Number) winner[2]).intValue();

                    Beer beer = beerDAO.findById(beerId);
                    if (beer == null) {
                        return null;
                    }

                    return new ResultDTO(
                            event.getId(),
                            event.getStartDate(),
                            beerId,
                            beerName,
                            beer.getSubmittedBy(),
                            totalPoints
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        ctx.json(history);
    }

}
