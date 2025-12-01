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
    private int id;
    private String name;
    private String brewery;
    private int eventId;
    private int submittedBy;

    public BeerDTO(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getName();
        this.brewery = beer.getBrewery();
        this.eventId = beer.getEvent().getId();
        this.submittedBy = beer.getSubmittedBy().getId();
    }
}
