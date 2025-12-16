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
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String code;   // e.g. "AB7F"

    @Enumerated(EnumType.STRING)
    private EventStatus status; // OPEN, VOTING, CLOSED

    private LocalDate nextEventAt; // for countdown ONLY

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<Beer> beers;
}

