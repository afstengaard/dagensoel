package dk.dagensoel.bootstrap;

import dk.dagensoel.daos.AdminUserDAO;
import dk.dagensoel.entities.AdminUser;
import dk.dagensoel.security.PasswordUtil;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AdminUserBootstrap {

    public static void seedAdminIfMissing() {

        String username =
                System.getenv("ADMIN_USERNAME") != null
                        ? System.getenv("ADMIN_USERNAME")
                        : System.getProperty("ADMIN_USERNAME");

        String password =
                System.getenv("ADMIN_PASSWORD") != null
                        ? System.getenv("ADMIN_PASSWORD")
                        : System.getProperty("ADMIN_PASSWORD");

        if (username == null || password == null) {
            System.out.println("Admin bootstrap skipped (no credentials provided)");
            return;
        }

        AdminUserDAO dao = new AdminUserDAO();

        if (dao.findByUsername(username) != null) {
            System.out.println("Admin user already exists");
            return;
        }

        AdminUser admin = AdminUser.builder()
                .username(username)
                .passwordHash(PasswordUtil.hash(password))
                .build();

        dao.create(admin);

        System.out.println("Admin user created");
    }
}

