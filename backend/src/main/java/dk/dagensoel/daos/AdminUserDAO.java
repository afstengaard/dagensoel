package dk.dagensoel.daos;

import dk.dagensoel.entities.AdminUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AdminUserDAO extends BaseDAO<AdminUser> {

    public AdminUserDAO() {
        super(AdminUser.class);
    }

    public AdminUser findByUsername(String username) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                            "SELECT u FROM AdminUser u WHERE u.username = :username",
                            AdminUser.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
