package dk.dagensoel.daos;

import dk.dagensoel.entities.User;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }
}
