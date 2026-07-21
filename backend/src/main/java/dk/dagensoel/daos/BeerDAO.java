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

    public List<Beer> searchByName(String query) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT b FROM Beer b " +
                                    "WHERE LOWER(b.name) LIKE LOWER(:query)",
                            Beer.class
                    )
                    .setParameter("query", "%" + query + "%")
                    .setMaxResults(10)
                    .getResultList();
        }
    }

    public List<Beer> findAllWithEvent() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                    "SELECT b FROM Beer b JOIN FETCH b.event",
                    Beer.class
            ).getResultList();
        }
    }

    /**
     * Deletes a single beer and any votes tied to it (no cascade is
     * wired from Beer to Vote, so they're removed explicitly first to
     * avoid a foreign key violation).
     */
    public void deleteBeer(Long id) {
        runInTransaction(em -> {
            em.createQuery("DELETE FROM Vote v WHERE v.beer.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            Beer beer = em.find(Beer.class, id);
            if (beer != null) {
                em.remove(beer);
            }
        });
    }

}
