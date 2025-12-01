package dk.dagensoel.dtos;

import dk.dagensoel.entities.Vote;
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
public class VoteDTO {
    private int id;
    private int userId;
    private int beerId;
    private int points;

    public VoteDTO(Vote vote) {
        this.id = vote.getId();
        this.userId = vote.getUser().getId();
        this.beerId = vote.getBeer().getId();
        this.points = vote.getPoints();
    }
}
