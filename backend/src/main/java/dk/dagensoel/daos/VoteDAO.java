package dk.dagensoel.daos;

import dk.dagensoel.config.HibernateConfig;
import dk.dagensoel.entities.Beer;
import dk.dagensoel.entities.Vote;
import jakarta.persistence.EntityManager;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteDAO extends BaseDAO<Vote> {

    public VoteDAO() {
        super(Vote.class);
    }

    public Vote create(Vote vote) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(vote);
            em.getTransaction().commit();
            return vote;
        } finally {
            em.close();
        }
    }

    public Beer findBeerById(long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Beer.class, id);
        }
    }
}
