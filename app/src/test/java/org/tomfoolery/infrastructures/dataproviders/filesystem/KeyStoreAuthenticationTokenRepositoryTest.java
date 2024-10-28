package org.tomfoolery.infrastructures.dataproviders.filesystem;

import lombok.val;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepositoryTest;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

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
        assertFalse(firstAuthenticationTokenRepository.containsToken());
        assertNull(firstAuthenticationTokenRepository.getToken());

        firstAuthenticationTokenRepository.saveToken(authenticationToken);
        assertTrue(firstAuthenticationTokenRepository.containsToken());

        val authenticationTokenRetrievedFromFirst = firstAuthenticationTokenRepository.getToken();
        assertNotNull(authenticationTokenRetrievedFromFirst);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromFirst);

        val secondAuthenticationTokenRepository = getAuthenticationTokenRepository();
        assertTrue(secondAuthenticationTokenRepository.containsToken());

        val authenticationTokenRetrievedFromSecond = secondAuthenticationTokenRepository.getToken();
        assertNotNull(authenticationTokenRetrievedFromSecond);
        assertEquals(authenticationToken, authenticationTokenRetrievedFromSecond);

        firstAuthenticationTokenRepository.removeToken();
        assertFalse(firstAuthenticationTokenRepository.containsToken());
        assertNull(firstAuthenticationTokenRepository.getToken());
        assertFalse(secondAuthenticationTokenRepository.containsToken());
        assertNull(secondAuthenticationTokenRepository.getToken());
    }
}