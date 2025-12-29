package dk.dagensoel.unit;

import dk.dagensoel.security.PasswordUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose: Test that BCrypt password hashing and verification works correctly
 *
 * @Author: Anton Friis Stengaard
 */
class PasswordUtilTest {

    @Test
    void passwordIsHashedAndVerifiedCorrectly() {
        String password = "secret123";

        String hash = PasswordUtil.hash(password);

        assertNotEquals(password, hash);
        assertTrue(PasswordUtil.verify(password, hash));
        assertFalse(PasswordUtil.verify("wrong", hash));
    }
}
