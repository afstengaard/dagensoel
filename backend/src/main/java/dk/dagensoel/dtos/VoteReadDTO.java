package dk.dagensoel.dtos;

import dk.dagensoel.entities.Vote;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteReadDTO {

    private long id;
    private long eventId;
    private long favoriteBeerId;
    private long secondFavoriteBeerId;

    public VoteReadDTO(Vote vote) {
        this.id = vote.getId();
        this.eventId = vote.getEvent().getId();
        this.favoriteBeerId = vote.getFavoriteBeer().getId();
        this.secondFavoriteBeerId = vote.getSecondFavoriteBeer().getId();
    }
}
