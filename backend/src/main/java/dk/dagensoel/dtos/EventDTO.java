package dk.dagensoel.dtos;

import dk.dagensoel.entities.Event;
import lombok.*;

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

    private long id;
    private int year;
    private long code;
    private boolean votingOpen;
    private String name;
    private String startDate;
    private String endDate;

    private List<BeerDTO> beers;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.year = event.getYear();
        this.code = event.getCode();
        this.votingOpen = event.isVotingOpen();
        this.name = event.getName();
        this.startDate = event.getStartDate().toString();
        this.endDate = event.getEndDate().toString();

        this.beers = event.getBeers()
                .stream()
                .map(BeerDTO::new)
                .toList();
    }
}
