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
    private int id;

    private int points; // 2 or 1

    @ManyToOne
    private User voter;

    @ManyToOne
    private Beer beer;
}