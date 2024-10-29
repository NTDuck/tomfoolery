package org.tomfoolery.infrastructures.dataproviders.hash.base64;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.infrastructures.dataproviders.base64.auth.security.Base64PasswordEncoder;

import static org.testng.Assert.*;

class Base64PasswordEncoderTest {
    private final @NonNull Base64PasswordEncoder passwordEncoder = Base64PasswordEncoder.of();

    @Test
    void testEncodePassword() {
        String password = "mySecretPassword";
        String encodedPassword = passwordEncoder.encode(password);

        // Validate that the encoded password is not null and not empty
        assertNotNull(encodedPassword);
        assertFalse(encodedPassword.isEmpty());

        // Validate that encoding the same password produces the same hash
        assertEquals(encodedPassword, passwordEncoder.encode(password));
    }

    @Test
    void testVerifyPassword() {
        String password = "mySecretPassword";
        String encodedPassword = passwordEncoder.encode(password);

        // Verify the password against the encoded password
        assertTrue(passwordEncoder.verify(password, encodedPassword));

        String incorrectPassword = "wrongPassword";

        // Verify the incorrect password against the encoded password
        assertFalse(passwordEncoder.verify(incorrectPassword, encodedPassword));
    }
}