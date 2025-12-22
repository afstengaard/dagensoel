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
    public LocalDate startDate;
    public List<BeerDTO> beers;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.code = event.getCode();
        this.status = event.getStatus();
        this.startDate = event.getStartDate();
        this.beers = event.getBeers() == null
                ? List.of()
                : event.getBeers()
                .stream()
                .map(BeerDTO::new)
                .toList();
    }

    public EventDTO(Event event, boolean includeBeers) {
        this.id = event.getId();
        this.name = event.getName();
        this.code = event.getCode();
        this.status = event.getStatus();
        this.startDate = event.getStartDate();

        if (includeBeers && event.getBeers() != null) {
            this.beers = event.getBeers()
                    .stream()
                    .map(BeerDTO::new)
                    .toList();
        } else {
            this.beers = List.of();
        }
    }

}

