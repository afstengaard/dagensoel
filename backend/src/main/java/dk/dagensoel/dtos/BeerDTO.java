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

    public Long id;
    public String name;
    public String brewery;
    public String country;
    public double abv;
    public String submittedBy;

    public BeerDTO(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getName();
        this.brewery = beer.getBrewery();
        this.country = beer.getCountry();
        this.abv = beer.getAbv();
        this.submittedBy = beer.getSubmittedBy();
    }
}

