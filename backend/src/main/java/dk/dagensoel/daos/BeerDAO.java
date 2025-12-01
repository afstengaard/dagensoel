package dk.dagensoel.daos;

import dk.dagensoel.entities.Beer;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerDAO extends BaseDAO<Beer> {

    public BeerDAO() {
        super(Beer.class);
    }
}
