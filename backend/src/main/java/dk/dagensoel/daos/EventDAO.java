package dk.dagensoel.daos;

import dk.dagensoel.entities.Event;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class EventDAO extends BaseDAO<Event> {

    public EventDAO() {
        super(Event.class);
    }
}
