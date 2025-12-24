package dk.dagensoel.dtos;

import dk.dagensoel.entities.Vote;
import dk.dagensoel.entities.VoteType;
/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteResponseDTO {

    public Long id;
    public Long beerId;
    public VoteType type;
    public int points;

    public VoteResponseDTO(Vote vote) {
        this.id = vote.getId();
        this.beerId = vote.getBeer().getId();
        this.type = vote.getType();
        this.points = vote.getPoints();
    }
}