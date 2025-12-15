package dk.dagensoel.security;

import io.jsonwebtoken.*;
import java.util.Date;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class JwtUtil {

    private static final String SECRET =
            System.getenv().getOrDefault("JWT_SECRET", "dev-secret");

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 6; // 6h

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static String validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
