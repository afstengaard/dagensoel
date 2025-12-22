package dk.dagensoel.controllers;

import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
import dk.dagensoel.dtos.EventDTO;
import dk.dagensoel.dtos.ResultDTO;
import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.EventStatus;
import io.javalin.http.Context;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

public class EventController {

    private final EventDAO dao = new EventDAO();
    private final VoteDAO voteDAO = new VoteDAO();

    // READ
    public void getByCode(Context ctx) {
        String code = ctx.pathParam("code");

        Event event = dao.findByCode(code);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        ctx.json(new EventDTO(event));
    }

    public void getHistory(Context ctx) {
        List<EventDTO> history = dao.findClosedEvents()
                .stream()
                .map(EventDTO::new)
                .toList();

        ctx.json(history);
    }

    public void getActive(Context ctx) {
        Event event = dao.findActiveEvent();
        if (event == null) {
            ctx.status(404).result("No active event");
            return;
        }

        ctx.json(new EventDTO(event, false)); // Exclude beers as they're unnecessary here.
    }

    // WRITE

    public void create(Context ctx) {
        if (dao.hasActiveEvent()) {
            ctx.status(400).result("An active event already exists");
            return;
        }
        EventDTO dto = ctx.bodyAsClass(EventDTO.class);

        Event event = new Event();
        event.setName(dto.name);
        event.setStatus(EventStatus.OPEN);
        event.setCode(dao.generateUniqueCode());
        event.setStartDate(dto.startDate);

        Event created = dao.create(event);
        ctx.status(201).json(new EventDTO(created));
    }

    public void updateStatus(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        EventDTO dto = ctx.bodyAsClass(EventDTO.class);

        Event event = dao.findById(id);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (!isValidTransition(event.getStatus(), dto.status)) {
            ctx.status(400).result("Invalid event status transition");
            return;
        }

        if (dto.status == EventStatus.VOTING) {
            Event active = dao.findActiveEvent();
            if (active != null && !active.getId().equals(event.getId())) {
                ctx.status(400).result("Another active event already exists");
                return;
            }
        }

        event.setStatus(dto.status);
        dao.update(event);

        ctx.json(new EventDTO(event,false));
    }

    // HELPERS

    private boolean isValidTransition(EventStatus current, EventStatus next) {
        return (current == EventStatus.OPEN && next == EventStatus.VOTING)
                || (current == EventStatus.VOTING && next == EventStatus.CLOSED);
    }

    public void getResults(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("id"));

        Event event = dao.findById(eventId);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        if (event.getStatus() != EventStatus.CLOSED) {
            ctx.status(403).result("Results are not available yet");
            return;
        }

        List<ResultDTO> results = voteDAO.getResultsForEvent(eventId)
                .stream()
                .map(row -> new ResultDTO(
                        (Long) row[0],
                        (String) row[1],
                        ((Number) row[2]).intValue()
                ))
                .toList();

        ctx.json(results);
    }
}
