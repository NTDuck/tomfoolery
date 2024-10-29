package org.tomfoolery.core.dataproviders.auth;

import lombok.val;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.abc.ReadonlyUser;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenGeneratorTest {
    protected abstract AuthenticationTokenGenerator getAuthenticationTokenGenerator();

    @Test
    public void testBasic() {
        val authenticationTokenGenerator = getAuthenticationTokenGenerator();

        val userId = ReadonlyUser.Id.of();
        val userClass = Administrator.class;
        val expiryTimestamp = LocalDateTime.now().plusDays(1);

        val token = authenticationTokenGenerator.generateToken(userId, userClass, expiryTimestamp);

        val isTokenValid = authenticationTokenGenerator.verifyToken(token);
        assertTrue(isTokenValid);

        val extractedUserId = authenticationTokenGenerator.getUserIdFromToken(token);
        assertEquals(extractedUserId, userId);

        val extractedUserClass = authenticationTokenGenerator.getUserClassFromToken(token);
        assertEquals(extractedUserClass, userClass);
    }
}