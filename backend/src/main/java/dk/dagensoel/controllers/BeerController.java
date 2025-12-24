package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.dtos.BeerDTO;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import io.javalin.http.Context;

import java.util.List;
/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerController {

    private final BeerDAO beerDAO = new BeerDAO();
    private final EventDAO eventDAO = new EventDAO();

    // READ

    // Public: get beers for an event
    public void getByEvent(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("eventId"));

        List<BeerDTO> beers = beerDAO.findByEvent(eventId)
                .stream()
                .map(BeerDTO::new)
                .toList();

        ctx.json(beers);
    }

    // WRITE (ADMIN)

    public void create(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("eventId"));
        BeerDTO dto = ctx.bodyAsClass(BeerDTO.class);

        Event event = eventDAO.findById(eventId);
        if (event == null) {
            ctx.status(404).result("Event not found");
            return;
        }

        Beer beer = new Beer();
        beer.setName(dto.name);
        beer.setBrewery(dto.brewery);
        beer.setCountry(dto.country);
        beer.setAbv(dto.abv);
        beer.setSubmittedBy(dto.submittedBy);
        beer.setEvent(event);

        Beer created = beerDAO.create(beer);
        ctx.status(201).json(new BeerDTO(created));
    }
}
