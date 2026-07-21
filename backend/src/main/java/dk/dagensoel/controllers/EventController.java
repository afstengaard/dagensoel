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

        ctx.json(new EventDTO(event, true));
    }

    public void getActive(Context ctx) {
        try {
            Event event = eventDAO.findActiveEvent();
            if (event == null) {
                ctx.status(404).result("No active event");
                return;
            }
            ctx.json(new EventDTO(event, true));
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
        ctx.status(201).json(new EventDTO(created, true));
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

    /**
     * Full event + beers for the admin edit page, with each beer's
     * combined point total (live votes + importedPoints) attached -
     * unlike the plain getByCode/getActive fetch, which doesn't need it.
     */
    public void getForEdit(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("id"));

        Event event = eventDAO.findById(eventId);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        java.util.Map<Long, Integer> pointsByBeerId = new java.util.HashMap<>();
        for (Object[] row : voteDAO.getResultsForEvent(eventId)) {
            Long beerId = ((Number) row[0]).longValue();
            int totalPoints = ((Number) row[2]).intValue();
            pointsByBeerId.put(beerId, totalPoints);
        }

        dk.dagensoel.dtos.EventDTO dto = new dk.dagensoel.dtos.EventDTO(event, true);
        for (dk.dagensoel.dtos.BeerDTO beerDto : dto.beers) {
            beerDto.totalPoints = pointsByBeerId.getOrDefault(beerDto.id, 0);
        }

        ctx.json(dto);
    }

    public void update(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        Event event = eventDAO.findById(id);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        EventDTO dto = ctx.bodyAsClass(EventDTO.class);
        event.setName(dto.name);
        event.setStartDate(dto.startDate);

        Event updated = eventDAO.update(event);
        ctx.json(new EventDTO(updated, false));
    }

    // HELPERS

    private boolean isValidTransition(EventStatus current, EventStatus next) {
        return (current == EventStatus.OPEN && next == EventStatus.VOTING)
                || (current == EventStatus.VOTING && next == EventStatus.CLOSED);
    }

    public void delete(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        Event event = eventDAO.findById(id);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (event.getStatus() != EventStatus.CLOSED) {
            ctx.status(409).result("Only closed events can be deleted");
            return;
        }

        eventDAO.deleteEvent(id);
        ctx.status(204);
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
                                    event.getName(),
                                    event.getStartDate(),
                                    beerId,
                                    beerName,
                                    beer.getBrewery(),
                                    beer.getAbv(),
                                    beer.getSubmittedBy(),
                                    beer.getImageUrl(),
                                    beer.getUntappdLink(),
                                    beer.getEvening(),
                                    totalPoints
                            );
                        })
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        // Results already come back ordered by totalPoints DESC from the
        // query - assign 1-based placements here, with tied beers sharing
        // a placement (e.g. two 2nd-place beers, then the next is 4th).
        int placement = 0;
        int previousPoints = Integer.MIN_VALUE;
        for (int i = 0; i < results.size(); i++) {
            ResultDTO dto = results.get(i);
            if (dto.totalPoints != previousPoints) {
                placement = i + 1;
                previousPoints = dto.totalPoints;
            }
            dto.placement = placement;
        }

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

                    ResultDTO dto = new ResultDTO(
                            event.getId(),
                            event.getName(),
                            event.getStartDate(),
                            beerId,
                            beerName,
                            beer.getBrewery(),
                            beer.getAbv(),
                            beer.getSubmittedBy(),
                            beer.getImageUrl(),
                            beer.getUntappdLink(),
                            beer.getEvening(),
                            totalPoints
                    );
                    dto.placement = 1; // getHistory only ever returns the winner
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();

        ctx.json(history);
    }

}
