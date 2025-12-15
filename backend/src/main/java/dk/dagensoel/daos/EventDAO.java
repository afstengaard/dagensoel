package dk.dagensoel.daos;

import dk.dagensoel.entities.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class EventDAO extends BaseDAO<Event> {

    public EventDAO() {
        super(Event.class);
    }

    //Find event by code.
    public Event findByCode(long code) {
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
}
