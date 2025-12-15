package dk.dagensoel.controllers;

import dk.dagensoel.daos.UserDAO;
import dk.dagensoel.dtos.AuthResponseDTO;
import dk.dagensoel.dtos.LoginDTO;
import dk.dagensoel.entities.User;
import dk.dagensoel.security.JwtUtil;
import dk.dagensoel.security.PasswordUtil;
import io.javalin.http.Context;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    public void login(Context ctx) {
        LoginDTO dto = ctx.bodyAsClass(LoginDTO.class);

        User user = userDAO.findByUsername(dto.getUsername());

        if (user == null ||
                !PasswordUtil.verify(dto.getPassword(), user.getPasswordHash())) {
            ctx.status(401).result("Invalid username or password");
            return;
        }

        String token = JwtUtil.generateToken(user.getUsername());
        ctx.json(new AuthResponseDTO(token));
    }
}
