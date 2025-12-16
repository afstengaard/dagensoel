package dk.dagensoel.daos;

import dk.dagensoel.entities.Beer;
import jakarta.persistence.EntityManager;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class BeerDAO extends BaseDAO<Beer> {

    public BeerDAO() {
        super(Beer.class);
    }

    public List<Beer> findByEvent(Long eventId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT b FROM Beer b WHERE b.event.id = :eventId",
                            Beer.class
                    )
                    .setParameter("eventId", eventId)
                    .getResultList();
        }
    }
}
