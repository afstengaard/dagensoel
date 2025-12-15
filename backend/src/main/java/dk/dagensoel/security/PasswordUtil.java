package dk.dagensoel.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
public class PasswordUtil {

    public static String hash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verify(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
