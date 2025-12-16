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
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"event_id", "deviceHash", "type"}
        )
)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Event event;

    @ManyToOne(optional = false)
    private Beer beer;

    @Column(nullable = false)
    private String deviceHash;

    @Enumerated(EnumType.STRING)
    private VoteType type;   // FAVORITE / SECOND

    private int points;      // 2 or 1
}
