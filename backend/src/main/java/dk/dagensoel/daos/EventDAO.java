package dk.dagensoel.daos;

import dk.dagensoel.entities.Event;
import dk.dagensoel.entities.EventStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.UUID;

public class EventDAO extends BaseDAO<Event> {

    public EventDAO() {
        super(Event.class);
    }

    public Event findByCode(String code) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM Event e LEFT JOIN FETCH e.beers WHERE e.code = :code",
                            Event.class
                    )
                    .setParameter("code", code)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Event> findClosedEvents() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT e FROM Event e WHERE e.status = :status ORDER BY e.id DESC",
                            Event.class
                    )
                    .setParameter("status", EventStatus.CLOSED)
                    .getResultList();
        }
    }

    public Event findActiveEvent() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Event> events = em.createQuery(
                            "SELECT e FROM Event e WHERE e.status IN (:open, :voting) ORDER BY e.id DESC",
                            Event.class
                    )
                    .setParameter("open", EventStatus.OPEN)
                    .setParameter("voting", EventStatus.VOTING)
                    .getResultList();

            if (events.isEmpty()) {
                return null;
            }

            if (events.size() > 1) {
                throw new IllegalStateException("More than one active event exists");
            }

            return events.get(0);
        }
    }

    public boolean hasActiveEvent() {
        try (EntityManager em = emf.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Event e WHERE e.status IN (:open, :voting)",
                            Long.class
                    )
                    .setParameter("open", EventStatus.OPEN)
                    .setParameter("voting", EventStatus.VOTING)
                    .getSingleResult();

            return count > 0;
        }
    }

    public String generateUniqueCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}
