package org.tomfoolery.core.dataproviders.auth;

import lombok.val;
import org.testng.annotations.Test;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.abc.ReadonlyUser;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenServiceTest {
    protected abstract AuthenticationTokenService getAuthenticationTokenService();

    @Test
    public void testBasic() {
        val authenticationTokenService = getAuthenticationTokenService();

        val userId = ReadonlyUser.Id.of();
        val userClass = Administrator.class;
        val expiryTimestamp = LocalDateTime.now().plusDays(1);

        val token = authenticationTokenService.generateToken(userId, userClass, expiryTimestamp);

        val isTokenValid = authenticationTokenService.verifyToken(token);
        assertTrue(isTokenValid);

        val extractedUserId = authenticationTokenService.getUserIdFromToken(token);
        assertEquals(extractedUserId, userId);

        val extractedUserClass = authenticationTokenService.getUserClassFromToken(token);
        assertEquals(extractedUserClass, userClass);
    }
}