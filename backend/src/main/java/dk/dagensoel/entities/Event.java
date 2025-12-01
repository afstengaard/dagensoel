package dk.dagensoel.entities;

import lombok.*;
import jakarta.persistence.*;
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
    private int id;

    private int year;     // e.g. 2024, 2025…

    @OneToMany(mappedBy = "event")
    private List<Beer> beers;
}
