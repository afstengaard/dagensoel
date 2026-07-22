package dk.dagensoel.controllers;

import dk.dagensoel.daos.BeerDAO;
import dk.dagensoel.daos.EventDAO;
import dk.dagensoel.daos.VoteDAO;
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
    private final VoteDAO voteDAO = new VoteDAO();

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
        List<Beer> beers = beerDAO.findAllWithEvent();

        // Group by event so each event's ranking is only computed once,
        // not once per beer.
        Map<Long, List<Beer>> beersByEvent = beers.stream()
                .collect(java.util.stream.Collectors.groupingBy(b -> b.getEvent().getId()));

        Map<Long, Integer> placementByBeerId = new java.util.HashMap<>();
        Map<Long, Integer> pointsByBeerId = new java.util.HashMap<>();
        java.util.Set<Long> eventsWithUnknownPoints = new java.util.HashSet<>();

        for (Long eventId : beersByEvent.keySet()) {
            List<Object[]> results = voteDAO.getResultsForEvent(eventId);

            boolean pointsUnknown = !results.isEmpty() && results.stream()
                    .allMatch(row -> ((Number) row[2]).intValue() == 0);
            if (pointsUnknown) {
                eventsWithUnknownPoints.add(eventId);
            }

            // Already ordered by totalPoints DESC - same standard
            // competition ranking as the per-event results page. Skipped
            // when points are unknown, since there's nothing real to rank.
            int placement = 0;
            int previousPoints = Integer.MIN_VALUE;
            for (int i = 0; i < results.size(); i++) {
                Object[] row = results.get(i);
                Long beerId = ((Number) row[0]).longValue();
                int totalPoints = ((Number) row[2]).intValue();
                if (!pointsUnknown) {
                    if (totalPoints != previousPoints) {
                        placement = i + 1;
                        previousPoints = totalPoints;
                    }
                    placementByBeerId.put(beerId, placement);
                }
                pointsByBeerId.put(beerId, totalPoints);
            }
        }

        List<BeerSearchDTO> dtos = beers.stream()
                .map(beer -> {
                    BeerSearchDTO dto = new BeerSearchDTO(beer);
                    dto.pointsUnknown = eventsWithUnknownPoints.contains(beer.getEvent().getId());
                    dto.placement = placementByBeerId.getOrDefault(beer.getId(), 0);
                    dto.totalPoints = pointsByBeerId.getOrDefault(beer.getId(), 0);
                    return dto;
                })
                .toList();

        ctx.json(dtos);
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
        beer.setUntappdLink(dto.untappdLink);
        beer.setEvening(dto.evening);
        beer.setStyle(dto.style);
        beer.setEvent(event);

        if (dto.imageUrl != null) {
            String imageUrl = dto.imageUrl.trim();
            if (!imageUrl.isEmpty() && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                ctx.status(400).result("imageUrl must be a full http(s) URL");
                return;
            }
            beer.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);
        }

        Beer created = beerDAO.create(beer);
        ctx.status(201).json(new BeerDTO(created));
    }

    /**
     * Full edit of a beer's fields, used by the admin event-edit page.
     * The incoming totalPoints is treated as the desired final total:
     * since live votes are anonymous and can't be edited directly, the
     * difference between that and the actual vote sum is stored in
     * importedPoints so the displayed total ends up exactly right.
     */
    public void update(Context ctx) {
        long beerId = Long.parseLong(ctx.pathParam("id"));

        Beer beer = beerDAO.findById(beerId);
        if (beer == null) {
            ctx.status(404).result("Beer not found");
            return;
        }

        BeerDTO dto = ctx.bodyAsClass(BeerDTO.class);

        String imageUrl = dto.imageUrl == null ? "" : dto.imageUrl.trim();
        if (!imageUrl.isEmpty() && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            ctx.status(400).result("imageUrl must be a full http(s) URL");
            return;
        }

        beer.setName(dto.name);
        beer.setBrewery(dto.brewery);
        beer.setCountry(dto.country);
        beer.setAbv(dto.abv);
        beer.setSubmittedBy(dto.submittedBy);
        beer.setUntappdLink(dto.untappdLink);
        beer.setEvening(dto.evening);
        beer.setStyle(dto.style);
        beer.setImageUrl(imageUrl.isEmpty() ? null : imageUrl);

        int liveVoteSum = voteDAO.getVoteSumForBeer(beerId);
        beer.setImportedPoints(dto.totalPoints - liveVoteSum);

        Beer updated = beerDAO.update(beer);

        BeerDTO response = new BeerDTO(updated);
        response.totalPoints = liveVoteSum
                + (updated.getImportedPoints() == null ? 0 : updated.getImportedPoints());
        ctx.json(response);
    }

    public void delete(Context ctx) {
        long beerId = Long.parseLong(ctx.pathParam("id"));

        Beer beer = beerDAO.findById(beerId);
        if (beer == null) {
            ctx.status(404).result("Beer not found");
            return;
        }

        beerDAO.deleteBeer(beerId);
        ctx.status(204);
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
