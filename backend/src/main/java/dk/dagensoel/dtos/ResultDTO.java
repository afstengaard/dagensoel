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

    public int totalPoints;

    /**
     * 1-based rank within the event, based on totalPoints (standard
     * "1224" competition ranking - tied beers share a placement and the
     * next distinct placement skips accordingly). Set after construction
     * once all of an event's results are known, so it defaults to 0
     * until then.
     */
    public int placement;

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
        this.totalPoints = totalPoints;
    }
}
