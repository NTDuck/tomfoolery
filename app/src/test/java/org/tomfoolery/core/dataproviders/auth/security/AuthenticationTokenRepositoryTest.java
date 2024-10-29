package org.tomfoolery.core.dataproviders.auth.security;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenRepositoryTest {
    protected static final @NonNull String PSEUDO_SERIALIZED_PAYLOAD = "eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.";

    protected abstract @NonNull AuthenticationTokenRepository getAuthenticationTokenRepository();

    @Test
    public void testBasic() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val authenticationTokenRepository = getAuthenticationTokenRepository();
        assertFalse(authenticationTokenRepository.contains());
        assertNull(authenticationTokenRepository.get());

        authenticationTokenRepository.save(authenticationToken);
        assertTrue(authenticationTokenRepository.contains());

        val retrievedAuthenticationToken = authenticationTokenRepository.get();
        assertNotNull(retrievedAuthenticationToken);
        assertEquals(authenticationToken, retrievedAuthenticationToken);

        authenticationTokenRepository.delete();
        assertFalse(authenticationTokenRepository.contains());
        assertNull(authenticationTokenRepository.get());
    }

    @Test
    public void testPersistence() {
        val authenticationToken = AuthenticationToken.of(PSEUDO_SERIALIZED_PAYLOAD);

        val firstAuthenticationTokenRepository = getAuthenticationTokenRepository();
        assertFalse(firstAuthenticationTokenRepository.contains());
        assertNull(firstAuthenticationTokenRepository.get());

        firstAuthenticationTokenRepository.save(authenticationToken);
        assertTrue(firstAuthenticationTokenRepository.contains());

        val authenticationTokenRetrievedFromFirst = firstAuthenticationTokenRepository.get();
        assertNotNull(authenticationTokenRetrievedFromFirst);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromFirst);

        val secondAuthenticationTokenRepository = getAuthenticationTokenRepository();
        assertTrue(secondAuthenticationTokenRepository.contains());

        val authenticationTokenRetrievedFromSecond = secondAuthenticationTokenRepository.get();
        assertNotNull(authenticationTokenRetrievedFromSecond);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromSecond);

        firstAuthenticationTokenRepository.delete();
        assertFalse(firstAuthenticationTokenRepository.contains());
        assertNull(firstAuthenticationTokenRepository.get());
        assertFalse(secondAuthenticationTokenRepository.contains());
        assertNull(secondAuthenticationTokenRepository.get());
    }
}