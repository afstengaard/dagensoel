package dk.dagensoel.dtos;

import java.time.LocalDate;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class ResultDTO {

    public Long eventId;
    public String eventName;
    public LocalDate eventDate;

    public Long beerId;
    public String beerName;
    public String brewery;
    public double abv;
    public String submittedBy;
    public String imageUrl;
    public String untappdLink;
    public String evening;
    public String style;

    public int totalPoints;

    /**
     * 1-based rank within the event, based on totalPoints (standard
     * "1224" competition ranking - tied beers share a placement and the
     * next distinct placement skips accordingly). Set after construction
     * once all of an event's results are known, so it defaults to 0
     * until then.
     */
    public int placement;

    /**
     * True when every beer in this event has 0 total points - meaning we
     * simply never recorded results for that year, rather than every
     * beer genuinely tying at 0. In that case there's no real winner or
     * ranking to show, and the frontend should display "Ukendt" instead
     * of a beer name or a "(0 p)" placement.
     */
    public boolean pointsUnknown;

    public ResultDTO(
            Long eventId,
            String eventName,
            LocalDate eventDate,
            Long beerId,
            String beerName,
            String brewery,
            double abv,
            String submittedBy,
            String imageUrl,
            String untappdLink,
            String evening,
            String style,
            int totalPoints
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.beerId = beerId;
        this.beerName = beerName;
        this.brewery = brewery;
        this.abv = abv;
        this.submittedBy = submittedBy;
        this.imageUrl = imageUrl;
        this.untappdLink = untappdLink;
        this.evening = evening;
        this.style = style;
        this.totalPoints = totalPoints;
    }
}
