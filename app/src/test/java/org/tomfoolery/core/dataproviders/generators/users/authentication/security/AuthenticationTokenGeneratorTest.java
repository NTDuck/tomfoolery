package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.annotations.Test;
import org.tomfoolery.abc.UnitTest;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

import java.time.Instant;
import java.util.UUID;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenGeneratorTest extends UnitTest<AuthenticationTokenGenerator> {
    private final BaseUser.@NonNull Id userId = BaseUser.Id.of(UUID.randomUUID());
    private final @NonNull Class<? extends BaseUser> userClass = Administrator.class;
    private final @NonNull Instant expiryTimestamp = Instant.now().plusMillis(444);

    private @Nullable AuthenticationToken cachedAuthenticationToken;

    @Test
    public void WhenGeneratingToken_ExpectValidToken() {
        this.cachedAuthenticationToken = this.unit.generate(this.userId, this.userClass, this.expiryTimestamp);

        assertTrue(this.unit.verify(this.cachedAuthenticationToken));
    }

    @Test(dependsOnMethods = { "WhenGeneratingToken_ExpectValidToken" })
    public void GivenValidToken_WhenRetrievingUserId_ExpectPresentAndMatchingUserId() {
        assert this.cachedAuthenticationToken != null;
        val retrievedUserId = this.unit.getUserIdFromAuthenticationToken(this.cachedAuthenticationToken);

        assertNotNull(retrievedUserId);
        assertEquals(retrievedUserId, this.userId);
    }

    @Test(dependsOnMethods = { "WhenGeneratingToken_ExpectValidToken" })
    public void GivenValidToken_WhenRetrievingUserClass_ExpectPresentAndMatchingUserClass() {
        assert this.cachedAuthenticationToken != null;
        val retrievedUserClass = this.unit.getUserClassFromAuthenticationToken(this.cachedAuthenticationToken);

        assertNotNull(retrievedUserClass);
        assertEquals(retrievedUserClass, this.userClass);
    }
}