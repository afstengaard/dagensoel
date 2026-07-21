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
    public String imageUrl;
    public String untappdLink;
    public String evening;

    /**
     * Combined point total (live votes + any imported points) for this
     * beer. Only populated by endpoints that compute it (the edit-event
     * fetch); zero otherwise. When sent back on an update, it's treated
     * as the desired final total - see BeerController.update().
     */
    public int totalPoints;

    public BeerDTO(Beer beer) {
        this.id = beer.getId();
        this.name = beer.getName();
        this.brewery = beer.getBrewery();
        this.country = beer.getCountry();
        this.abv = beer.getAbv();
        this.submittedBy = beer.getSubmittedBy();
        this.imageUrl = beer.getImageUrl();
        this.untappdLink = beer.getUntappdLink();
        this.evening = beer.getEvening();
    }
}

