package org.tomfoolery.core.dataproviders.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenRepositoryTest {
    protected static final @NonNull String PSEUDO_SERIALIZED_PAYLOAD = "eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.";

    protected abstract @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository();

    @Test
    public void testBasic() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val authenticationTokenRepository = getAuthenticationTokenAuthenticationTokenRepository();
        assertFalse(authenticationTokenRepository.containsAuthenticationToken());
        assertNull(authenticationTokenRepository.getAuthenticationToken());

        authenticationTokenRepository.saveAuthenticationToken(authenticationToken);
        assertTrue(authenticationTokenRepository.containsAuthenticationToken());

        val retrievedAuthenticationToken = authenticationTokenRepository.getAuthenticationToken();
        assertNotNull(retrievedAuthenticationToken);
        assertEquals(authenticationToken, retrievedAuthenticationToken);

        authenticationTokenRepository.deleteAuthenticationToken();
        assertFalse(authenticationTokenRepository.containsAuthenticationToken());
        assertNull(authenticationTokenRepository.getAuthenticationToken());
    }

    @Test
    public void testPersistence() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val firstAuthenticationTokenRepository = getAuthenticationTokenAuthenticationTokenRepository();
        assertFalse(firstAuthenticationTokenRepository.containsAuthenticationToken());
        assertNull(firstAuthenticationTokenRepository.getAuthenticationToken());

        firstAuthenticationTokenRepository.saveAuthenticationToken(authenticationToken);
        assertTrue(firstAuthenticationTokenRepository.containsAuthenticationToken());

        val authenticationTokenRetrievedFromFirst = firstAuthenticationTokenRepository.getAuthenticationToken();
        assertNotNull(authenticationTokenRetrievedFromFirst);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromFirst);

        val secondAuthenticationTokenRepository = getAuthenticationTokenAuthenticationTokenRepository();
        assertTrue(secondAuthenticationTokenRepository.containsAuthenticationToken());

        val authenticationTokenRetrievedFromSecond = secondAuthenticationTokenRepository.getAuthenticationToken();
        assertNotNull(authenticationTokenRetrievedFromSecond);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromSecond);

        firstAuthenticationTokenRepository.deleteAuthenticationToken();
        assertFalse(firstAuthenticationTokenRepository.containsAuthenticationToken());
        assertNull(firstAuthenticationTokenRepository.getAuthenticationToken());
        assertFalse(secondAuthenticationTokenRepository.containsAuthenticationToken());
        assertNull(secondAuthenticationTokenRepository.getAuthenticationToken());
    }
}