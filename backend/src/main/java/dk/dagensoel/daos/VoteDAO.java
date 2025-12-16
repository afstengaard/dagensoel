package dk.dagensoel.daos;

import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.Vote;
import dk.dagensoel.entities.VoteType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class VoteDAO extends BaseDAO<Vote> {

    public VoteDAO() {
        super(Vote.class);
    }

    public boolean hasVoted(Event event, String deviceHash, VoteType type) {
        try (EntityManager em = emf.createEntityManager()) {
            em.createQuery(
                            "SELECT v FROM Vote v WHERE v.event = :event AND v.deviceHash = :deviceHash AND v.type = :type",
                            Vote.class
                    )
                    .setParameter("event", event)
                    .setParameter("deviceHash", deviceHash)
                    .setParameter("type", type)
                    .getSingleResult();

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<Object[]> getResultsForEvent(Long eventId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            """
                            SELECT v.beer.id, v.beer.name, SUM(v.points)
                            FROM Vote v
                            WHERE v.event.id = :eventId
                            GROUP BY v.beer.id, v.beer.name
                            ORDER BY SUM(v.points) DESC
                            """,
                            Object[].class
                    )
                    .setParameter("eventId", eventId)
                    .getResultList();
        }
    }
}
