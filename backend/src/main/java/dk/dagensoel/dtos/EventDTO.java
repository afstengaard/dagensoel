package dk.dagensoel.dtos;

import dk.dagensoel.entities.Event;
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
public class EventDTO {
    private int id;
    private String name;
    private String startDate;
    private String endDate;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.startDate = event.getStartDate().toString();
        this.endDate = event.getEndDate().toString();
    }
}
