package dk.dagensoel.dtos;

import dk.dagensoel.entities.Beer;
import lombok.*;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDTO {
    private long id;
    private String name;
    private String brewery;
    private String country;
    private double abv;
    private long eventId;
    private String submittedBy;

    public BeerDTO(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getName();
        this.brewery = beer.getBrewery();
        this.country = beer.getCountry();
        this.abv = beer.getAbv();
        this.eventId = beer.getEvent().getId();
        this.submittedBy = beer.getSubmittedBy();
    }
}
