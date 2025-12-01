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
    private int id;

    private String name;
    private String brewery;

    @ManyToOne
    private User submittedBy;

    @ManyToOne
    private Event event;
}
