package org.tomfoolery.core.dataproviders.repositories.users.authentication.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.tomfoolery.abc.BaseUnitTest;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;

import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

@Test(groups = { "unit", "repository", "authentication" })
public abstract class AuthenticationTokenRepositoryTest extends BaseUnitTest<AuthenticationTokenRepository> {
    @Test(dataProvider = "AuthenticationTokenRepositoryTestDataProvider")
    void WhenSavingToken_ExpectPresentAndMatchingToken(@NonNull AuthenticationToken authenticationToken) {
        this.testSubject.save(authenticationToken);
        this.assertPresentAndMatchingToken(authenticationToken);
    }

    @Test(dataProvider = "AuthenticationTokenRepositoryTestDataProvider", dependsOnMethods = { "WhenSavingToken_ExpectPresentAndMatchingToken" })
    void GivenPresentToken_WhenSavingToken_ExpectPresentAndMatchingToken(@NonNull AuthenticationToken authenticationToken) {
        this.testSubject.save(authenticationToken);
        this.assertPresentAndMatchingToken(authenticationToken);
    }

    @Test(dependsOnMethods = { "GivenPresentToken_WhenSavingToken_ExpectPresentAndMatchingToken" })
    void GivenPresentToken_WhenRemovingToken_ExpectAbsentToken() {
        this.testSubject.remove();
        this.assertAbsentToken();
    }

    @Test(dependsOnMethods = { "GivenPresentToken_WhenRemovingToken_ExpectAbsentToken" })
    void GivenAbsentToken_WhenRemovingToken_ExpectAbsentToken() {
        this.testSubject.remove();
        this.assertAbsentToken();
    }

    private void assertPresentAndMatchingToken(@NonNull AuthenticationToken expectedAuthenticationToken) {
        assertTrue(this.testSubject.contains());

        val retrievedAuthenticationToken = this.testSubject.get();
        assertNotNull(retrievedAuthenticationToken);
        assertEquals(retrievedAuthenticationToken, expectedAuthenticationToken);
    }

    private void assertAbsentToken() {
        assertFalse(this.testSubject.contains());
        assertNull(this.testSubject.get());
    }

    @DataProvider(name = "AuthenticationTokenRepositoryTestDataProvider")
    public @NonNull Iterator<Object[]> createData() {
        return List.of(
            "eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.",
            "Hello World!"
        ).parallelStream()
            .map(SecureString::of)
            .map(AuthenticationToken::of)
            .map(authenticationToken -> new Object[] { authenticationToken })
            .iterator();
    }
}
