package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

import java.time.Instant;
import java.util.UUID;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "authentication" })
public abstract class AuthenticationTokenGeneratorTest extends BaseUnitTest<AuthenticationTokenGenerator> {
    private final BaseUser.@NonNull Id userId = BaseUser.Id.of(UUID.randomUUID());
    private final @NonNull Class<? extends BaseUser> userClass = Administrator.class;
    private final @NonNull Instant expiryTimestamp = Instant.now().plusMillis(4444);

    private @Nullable AuthenticationToken cachedAuthenticationToken;

    @Test
    public void WhenGeneratingToken_ExpectValidToken() {
        this.cachedAuthenticationToken = this.testSubject.generate(this.userId, this.userClass, this.expiryTimestamp);

        assertNotNull(this.cachedAuthenticationToken);
        assertTrue(this.testSubject.verify(this.cachedAuthenticationToken));
    }

    @Test
    public void GivenExpiredToken_WhenVerifyingToken_ExpectInvalidToken() {
        val expiredAuthenticationToken = this.testSubject.generate(this.userId, this.userClass, Instant.now().minusNanos(1));

        assertFalse(this.testSubject.verify(expiredAuthenticationToken));
    }

    @Test(dependsOnMethods = { "WhenGeneratingToken_ExpectValidToken" })
    public void GivenValidToken_WhenRetrievingUserId_ExpectPresentAndMatchingUserId() {
        assert this.cachedAuthenticationToken != null;
        val retrievedUserId = this.testSubject.getUserIdFromAuthenticationToken(this.cachedAuthenticationToken);

        assertNotNull(retrievedUserId);
        assertEquals(retrievedUserId, this.userId);
    }

    @Test(dependsOnMethods = { "GivenValidToken_WhenRetrievingUserId_ExpectPresentAndMatchingUserId" })
    public void GivenValidToken_WhenRetrievingUserClass_ExpectPresentAndMatchingUserClass() {
        assert this.cachedAuthenticationToken != null;
        val retrievedUserClass = this.testSubject.getUserClassFromAuthenticationToken(this.cachedAuthenticationToken);

        assertNotNull(retrievedUserClass);
        assertEquals(retrievedUserClass, this.userClass);
    }
}
