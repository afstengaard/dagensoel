package dk.dagensoel.daos;

import dk.dagensoel.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsername(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username",
                            User.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
