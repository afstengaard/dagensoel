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

    public int totalPoints;

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
        this.totalPoints = totalPoints;
    }
}
