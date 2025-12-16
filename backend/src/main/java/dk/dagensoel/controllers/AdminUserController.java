package dk.dagensoel.controllers;

import dk.dagensoel.daos.AdminUserDAO;
import dk.dagensoel.entities.AdminUser;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AdminUserController {

    private final AdminUserDAO dao = new AdminUserDAO();

    /**
     * Create an admin user.
     * This should only be enabled for initial setup
     * or protected by admin authentication.
     */
    public void create(Context ctx) {
        AdminUser adminUser = ctx.bodyAsClass(AdminUser.class);

        // Optional: basic validation
        if (adminUser.getUsername() == null || adminUser.getPasswordHash() == null) {
            ctx.status(400).result("Username and password are required");
            return;
        }

        AdminUser created = dao.create(adminUser);
        ctx.status(201).json(created);
    }

    /**
     * Get the currently authenticated admin user.
     * Requires JWT middleware.
     */
    public void me(Context ctx) {
        String username = ctx.attribute("username"); // set by JWT filter

        AdminUser adminUser = dao.findByUsername(username);
        if (adminUser == null) {
            ctx.status(404).result("Admin user not found");
            return;
        }

        ctx.json(adminUser);
    }
}