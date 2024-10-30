package org.tomfoolery.core.dataproviders.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;

import java.time.LocalDateTime;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenGeneratorTest {
    protected abstract @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator();

    @Test
    public void testBasic() {
        val authenticationTokenGenerator = getAuthenticationTokenGenerator();

        val userId = BaseUser.Id.of();
        val userClass = Administrator.class;
        val expiryTimestamp = LocalDateTime.now().plusDays(1);

        val token = authenticationTokenGenerator.generateAuthenticationToken(userId, userClass, expiryTimestamp);

        val isTokenValid = authenticationTokenGenerator.verifyAuthenticationToken(token);
        assertTrue(isTokenValid);

        val extractedUserId = authenticationTokenGenerator.getUserIdFromAuthenticationToken(token);
        assertEquals(extractedUserId, userId);

        val extractedUserClass = authenticationTokenGenerator.getUserClassFromAuthenticationToken(token);
        assertEquals(extractedUserClass, userClass);
    }
}