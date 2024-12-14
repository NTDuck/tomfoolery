package org.tomfoolery.core.dataproviders.generators.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "authentication" })
public abstract class AuthenticationTokenGeneratorTest extends BaseUnitTest<AuthenticationTokenGenerator> {
    private static final @Unsigned int NUMBER_OF_INVOCATIONS = 10;

    @Test(dataProvider = "AuthenticationTokenGeneratorTestValidDataProvider")
    void GivenValidParams_WhenGeneratingToken_ExpectValidToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp) {
        val generatedAuthenticationToken = this.testSubject.generate(userId, userClass, expiryTimestamp);

        assertNotNull(generatedAuthenticationToken);
        assertTrue(this.testSubject.verify(generatedAuthenticationToken));

        val retrievedUserid = this.testSubject.getUserIdFromAuthenticationToken(generatedAuthenticationToken);
        assertNotNull(retrievedUserid);
        assertEquals(retrievedUserid, userId);

        val retrievedUserClass = this.testSubject.getUserClassFromAuthenticationToken(generatedAuthenticationToken);
        assertNotNull(retrievedUserClass);
        assertEquals(retrievedUserClass, userClass);
    }

    @Test(dataProvider = "AuthenticationTokenGeneratorTestInvalidDataProvider")
    void GivenExpiredToken_WhenVerifyingToken_ExpectInvalidToken(BaseUser.@NonNull Id userId, @NonNull Class<? extends BaseUser> userClass, @NonNull Instant expiryTimestamp) {
        val generatedAuthenticationToken = this.testSubject.generate(userId, userClass, expiryTimestamp);

        assertNotNull(generatedAuthenticationToken);
        assertFalse(this.testSubject.verify(generatedAuthenticationToken));
    }

    @DataProvider(name = "AuthenticationTokenGeneratorTestValidDataProvider", parallel = true)
    public @NonNull Iterator<Object[]> createValidData() {
        val userClassesPool = List.of(Administrator.class, Patron.class, Staff.class);

        return IntStream.range(0, NUMBER_OF_INVOCATIONS).parallel()
            .mapToObj(_ -> new Object[] {
                BaseUser.Id.of(UUID.randomUUID()),
                getRandom(userClassesPool),
                Instant.now().plusMillis(4444)
            })
            .iterator();
    }

    @DataProvider(name = "AuthenticationTokenGeneratorTestInvalidDataProvider", parallel = true)
    public @NonNull Iterator<Object[]> createInvalidData() {
        val userClassesPool = List.of(Administrator.class, Patron.class, Staff.class);

        return IntStream.range(0, NUMBER_OF_INVOCATIONS).parallel()
            .mapToObj(_ -> new Object[] {
                BaseUser.Id.of(UUID.randomUUID()),
                getRandom(userClassesPool),
                Instant.now().minusNanos(getRandom(1, 4444))
            })
            .iterator();
    }

    private static <T> @NonNull T getRandom(@NonNull List<T> ts) {
        return ts.get((int) (Math.random() * ts.size()));
    }

    private static @Unsigned int getRandom(@Unsigned int min, @Unsigned int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }
}
