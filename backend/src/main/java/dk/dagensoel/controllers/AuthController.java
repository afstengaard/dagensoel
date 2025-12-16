package dk.dagensoel.controllers;

import dk.dagensoel.daos.AdminUserDAO;
import dk.dagensoel.dtos.AuthResponseDTO;
import dk.dagensoel.dtos.LoginDTO;
import dk.dagensoel.entities.AdminUser;
import dk.dagensoel.security.JwtUtil;
import dk.dagensoel.security.PasswordUtil;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AuthController {

    private final AdminUserDAO adminUserDAO = new AdminUserDAO();

    public void login(Context ctx) {
        LoginDTO dto = ctx.bodyAsClass(LoginDTO.class);

        AdminUser adminUser = adminUserDAO.findByUsername(dto.getUsername());

        if (adminUser == null ||
                !PasswordUtil.verify(dto.getPassword(), adminUser.getPasswordHash())) {
            ctx.status(401).result("Invalid username or password");
            return;
        }

        String token = JwtUtil.generateToken(adminUser.getUsername());
        ctx.json(new AuthResponseDTO(token));
    }
}
