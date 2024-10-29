package org.tomfoolery.infrastructures.dataproviders.filesystem;

import lombok.val;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepositoryTest;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.infrastructures.dataproviders.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;

import static org.testng.Assert.*;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    protected AuthenticationTokenRepository getAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }

    @Test
    public void testPersistence() {
        val serializedPayload = "eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.";
        val authenticationToken = AuthenticationToken.of(serializedPayload);

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