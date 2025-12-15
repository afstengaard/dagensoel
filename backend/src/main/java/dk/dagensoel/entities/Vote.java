package dk.dagensoel.entities;

import lombok.*;
import jakarta.persistence.*;

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
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Beer favoriteBeer;

    @ManyToOne(optional = false)
    private Beer secondFavoriteBeer;

    @ManyToOne(optional = false)
    private Event event;

}