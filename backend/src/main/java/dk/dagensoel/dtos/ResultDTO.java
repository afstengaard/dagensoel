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
    public String submittedBy;

    public int totalPoints;

    public ResultDTO(
            Long eventId,
            String eventName,
            LocalDate eventDate,
            Long beerId,
            String beerName,
            String submittedBy,
            int totalPoints
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.beerId = beerId;
        this.beerName = beerName;
        this.submittedBy = submittedBy;
        this.totalPoints = totalPoints;
    }
}

