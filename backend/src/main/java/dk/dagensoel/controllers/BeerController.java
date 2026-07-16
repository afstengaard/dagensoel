package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.dtos.BeerDTO;
import dk.dagensoel.dtos.BeerSearchDTO;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Event;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;
/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerController {

    private final BeerDAO beerDAO = new BeerDAO();
    private final EventDAO eventDAO = new EventDAO();

    // READ

    public void getByEvent(Context ctx) {
        Long eventId = Long.parseLong(ctx.pathParam("eventId"));

        List<BeerDTO> beers = beerDAO.findByEvent(eventId)
                .stream()
                .map(BeerDTO::new)
                .toList();

        ctx.json(beers);
    }

    public void search(Context ctx) {
        String query = ctx.queryParam("q");

        if (query == null || query.trim().length() < 2) {
            ctx.json(List.of());
            return;
        }

        List<BeerSearchDTO> results = beerDAO.searchByName(query.trim())
                .stream()
                .map(BeerSearchDTO::new)
                .toList();

        ctx.json(results);
    }

    public void getHistory(Context ctx) {
        List<BeerSearchDTO> beers = beerDAO.findAllWithEvent()
                .stream()
                .map(BeerSearchDTO::new)
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

    /**
     * Sets (or clears, if blank) the image URL for a beer. We store a
     * direct link to an externally-hosted image rather than accepting a
     * file upload, since the app server's own disk isn't persistent on
     * Render's free tier.
     */
    public void setImageUrl(Context ctx) {
        long beerId = Long.parseLong(ctx.pathParam("id"));

        Beer beer = beerDAO.findById(beerId);
        if (beer == null) {
            ctx.status(404).result("Beer not found");
            return;
        }

        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String imageUrl = body.get("imageUrl");

        if (imageUrl != null) {
            imageUrl = imageUrl.trim();
            if (!imageUrl.isEmpty() && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                ctx.status(400).result("imageUrl must be a full http(s) URL");
                return;
            }
        }

        beer.setImageUrl((imageUrl == null || imageUrl.isEmpty()) ? null : imageUrl);
        Beer updated = beerDAO.update(beer);

        ctx.json(new BeerDTO(updated));
    }
}
