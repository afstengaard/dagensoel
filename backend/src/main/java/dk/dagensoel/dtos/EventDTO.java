package dk.dagensoel.dtos;

import dk.dagensoel.entities.EventStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
public class EventDTO {

    public Long id;
    public String name;
    public String code;
    public EventStatus status;
    public LocalDate nextEventAt;
    public List<BeerDTO> beers;
}

