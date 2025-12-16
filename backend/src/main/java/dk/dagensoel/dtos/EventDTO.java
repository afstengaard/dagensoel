package dk.dagensoel.dtos;

import dk.dagensoel.entities.Event;
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

    public EventDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.code = event.getCode();
        this.status = event.getStatus();
        this.nextEventAt = event.getNextEventAt();
        this.beers = event.getBeers() == null
                ? List.of()
                : event.getBeers()
                .stream()
                .map(BeerDTO::new)
                .toList();
    }
}

