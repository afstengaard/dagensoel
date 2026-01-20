package dk.dagensoel.daos;

import dk.dagensoel.entities.Beer;
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
            Long count = em.createQuery(
                            """
                            SELECT COUNT(v)
                            FROM Vote v
                            WHERE v.event = :event
                              AND v.deviceHash = :deviceHash
                              AND v.type = :type
                            """,
                            Long.class
                    )
                    .setParameter("event", event)
                    .setParameter("deviceHash", deviceHash)
                    .setParameter("type", type)
                    .getSingleResult();

            return count > 0;
        }
    }

    public List<Object[]> getResultsForEvent(Long eventId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            """
                            SELECT b.id, b.name, COALESCE(SUM(v.points), 0)
                            FROM Beer b
                            LEFT JOIN Vote v
                                ON v.beer = b
                               AND v.event.id = :eventId
                            WHERE b.event.id = :eventId
                            GROUP BY b.id, b.name
                            ORDER BY COALESCE(SUM(v.points), 0) DESC
                            """,
                            Object[].class
                    )
                    .setParameter("eventId", eventId)
                    .getResultList();
        }
    }


    public void createVotePair(
            Event event,
            Beer favorite,
            Beer second,
            String deviceHash
    ) {
        runInTransaction(em -> {
            em.persist(Vote.builder()
                    .event(event)
                    .beer(favorite)
                    .deviceHash(deviceHash)
                    .type(VoteType.FAVORITE)
                    .points(2)
                    .build());

            em.persist(Vote.builder()
                    .event(event)
                    .beer(second)
                    .deviceHash(deviceHash)
                    .type(VoteType.SECOND)
                    .points(1)
                    .build());
        });
    }


}
