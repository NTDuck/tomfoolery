package org.tomfoolery.infrastructures.dataproviders.hash.base64;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

class BasicHashPasswordServiceTest {
    private final @NonNull Base64PasswordService passwordService = Base64PasswordService.of();

    @Test
    void testEncodePassword() {
        String password = "mySecretPassword";
        String encodedPassword = passwordService.encodePassword(password);

        // Validate that the encoded password is not null and not empty
        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());

        // Validate that encoding the same password produces the same hash
        assertEquals(encodedPassword, passwordService.encodePassword(password));
    }

    @Test
    void testVerifyPassword() {
        String password = "mySecretPassword";
        String encodedPassword = passwordService.encodePassword(password);

        // Verify the password against the encoded password
        assertTrue(passwordService.verifyPassword(password, encodedPassword));

        String incorrectPassword = "wrongPassword";

        // Verify the incorrect password against the encoded password
        assertFalse(passwordService.verifyPassword(incorrectPassword, encodedPassword));
    }
}