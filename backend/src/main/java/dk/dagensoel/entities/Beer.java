package dk.dagensoel.entities;

import jakarta.persistence.*;
import lombok.*;

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
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String brewery;
    private String country;
    private double abv;
    private String submittedBy;

    @ManyToOne(optional = false)
    private Event event;
}
