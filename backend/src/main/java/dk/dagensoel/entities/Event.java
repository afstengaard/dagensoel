package dk.dagensoel.entities;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int year;
    private long code;
    private boolean votingOpen;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "event")
    private List<Beer> beers;
}
