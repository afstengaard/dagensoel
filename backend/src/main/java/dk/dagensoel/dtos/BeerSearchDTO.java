package dk.dagensoel.dtos;

import dk.dagensoel.entities.Beer;
import lombok.*;
/**
 * Purpose: Used when a user checks if a beer has been tasted before.
 *
 * @Author: Anton Friis Stengaard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerSearchDTO {

    public Long beerId;
    public String beerName;
    public Long eventId;
    public String eventName;

    public BeerSearchDTO(Beer beer) {
        this.beerId = beer.getId();
        this.beerName = beer.getName();
        this.eventId = beer.getEvent().getId();
        this.eventName = beer.getEvent().getName();
    }
}
