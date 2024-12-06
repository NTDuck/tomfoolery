package org.tomfoolery.core.dataproviders.repositories.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tomfoolery.abc.UnitTest;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenRepositoryTest extends UnitTest<AuthenticationTokenRepository> {
    private static final @NonNull SecureString PSEUDO_SERIALIZED_PAYLOAD = SecureString.of("eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.");

    private final @NonNull AuthenticationToken authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

    @BeforeClass
    public void setUp() {
        super.setUp();
    }

    @Test
    public void WhenSavingToken_ExpectPresentToken() {
        this.unit.saveAuthenticationToken(authenticationToken);

        assertTrue(this.unit.containsAuthenticationToken());
    }

    @Test(dependsOnMethods = { "WhenSavingToken_ExpectPresentToken" })
    public void GivenTokenIsSaved_WhenRetrievingToken_ExpectPresentAndMatchingToken() {
        val retrievedAuthenticationToken = this.unit.getAuthenticationToken();

        assertNotNull(retrievedAuthenticationToken);
        assertEquals(retrievedAuthenticationToken, this.authenticationToken);
    }

    @Test(dependsOnMethods = { "GivenTokenIsSaved_WhenRetrievingToken_ExpectPresentAndMatchingToken" })
    public void WhenRemovingToken_ExpectAbsentToken() {
        this.unit.removeAuthenticationToken();

        assertFalse(this.unit.containsAuthenticationToken());
        assertNull(this.unit.getAuthenticationToken());
    }
}