package org.tomfoolery.core.dataproviders.repositories.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "authentication" }, singleThreaded = true)
public abstract class AuthenticationTokenRepositoryTest extends BaseUnitTest<AuthenticationTokenRepository> {
    private static final @NonNull SecureString PSEUDO_SERIALIZED_PAYLOAD = SecureString.of("eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.");

    private final @NonNull AuthenticationToken authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

    @Test
    public void WhenSavingToken_ExpectPresentToken() {
        this.testSubject.save(authenticationToken);

        assertTrue(this.testSubject.contains());
    }

    @Test
    public void GivenTokenIsSaved_WhenRetrievingToken_ExpectPresentAndMatchingToken() {
        val retrievedAuthenticationToken = this.testSubject.get();

        assertNotNull(retrievedAuthenticationToken);
        assertEquals(retrievedAuthenticationToken, this.authenticationToken);
    }

    @Test
    public void WhenRemovingToken_ExpectAbsentToken() {
        this.testSubject.remove();

        assertFalse(this.testSubject.contains());
        assertNull(this.testSubject.get());
    }
}