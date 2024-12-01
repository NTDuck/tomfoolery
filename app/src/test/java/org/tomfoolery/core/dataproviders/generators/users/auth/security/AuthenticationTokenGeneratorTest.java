package org.tomfoolery.core.dataproviders.generators.users.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.time.Instant;
import java.util.UUID;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenGeneratorTest {
    protected abstract @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator();

    @Test
    public void testDefaultBehaviour() {
        val authenticationTokenGenerator = getAuthenticationTokenGenerator();

        val userId = BaseUser.Id.of(UUID.randomUUID());
        val userClass = Administrator.class;
        val expiryTimestamp = Instant.now().plusSeconds(1);

        val token = authenticationTokenGenerator.generate(userId, userClass, expiryTimestamp);

        val isTokenValid = authenticationTokenGenerator.verify(token);
        assertTrue(isTokenValid);

        val extractedUserId = authenticationTokenGenerator.getUserIdFromAuthenticationToken(token);
        assertEquals(extractedUserId, userId);

        val extractedUserClass = authenticationTokenGenerator.getUserClassFromAuthenticationToken(token);
        assertEquals(extractedUserClass, userClass);
    }
}