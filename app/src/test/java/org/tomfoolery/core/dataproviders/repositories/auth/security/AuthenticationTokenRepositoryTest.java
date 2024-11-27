package org.tomfoolery.core.dataproviders.repositories.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenRepositoryTest {
    protected static final @NonNull SecureString PSEUDO_SERIALIZED_PAYLOAD = SecureString.of("eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.");

    protected abstract @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository();

    @Test
    public void testDefaultBehaviour() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val authenticationTokenRepository = getAuthenticationTokenAuthenticationTokenRepository();

        authenticationTokenRepository.saveAuthenticationToken(authenticationToken);
        assertTrue(authenticationTokenRepository.containsAuthenticationToken());

        val retrievedAuthenticationToken = authenticationTokenRepository.getAuthenticationToken();
        assertNotNull(retrievedAuthenticationToken);
        assertEquals(authenticationToken, retrievedAuthenticationToken);

        authenticationTokenRepository.removeAuthenticationToken();
        assertFalse(authenticationTokenRepository.containsAuthenticationToken());
        assertNull(authenticationTokenRepository.getAuthenticationToken());
    }

    @Test
    public void testPersistence() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val firstAuthenticationTokenRepository = getAuthenticationTokenAuthenticationTokenRepository();

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

        firstAuthenticationTokenRepository.removeAuthenticationToken();
        assertFalse(firstAuthenticationTokenRepository.containsAuthenticationToken());
        assertNull(firstAuthenticationTokenRepository.getAuthenticationToken());
        assertFalse(secondAuthenticationTokenRepository.containsAuthenticationToken());
        assertNull(secondAuthenticationTokenRepository.getAuthenticationToken());
    }
}