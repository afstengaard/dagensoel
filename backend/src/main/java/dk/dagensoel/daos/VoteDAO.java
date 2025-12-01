package dk.dagensoel.daos;

import dk.dagensoel.entities.Vote;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteDAO extends BaseDAO<Vote> {

    public VoteDAO() {
        super(Vote.class);
    }
}
