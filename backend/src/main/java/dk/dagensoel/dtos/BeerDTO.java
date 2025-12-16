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
}

