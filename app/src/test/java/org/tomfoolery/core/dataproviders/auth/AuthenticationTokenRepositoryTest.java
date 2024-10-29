package org.tomfoolery.core.dataproviders.auth;

import lombok.val;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import static org.testng.Assert.*;

public abstract class AuthenticationTokenRepositoryTest {
    protected abstract AuthenticationTokenRepository getAuthenticationTokenRepository();

    @Test
    public void testBasic() {
        val serializedPayload = "eyJhbGciOiJub25lIn0.VGhlIHRydWUgc2lnbiBvZiBpbnRlbGxpZ2VuY2UgaXMgbm90IGtub3dsZWRnZSBidXQgaW1hZ2luYXRpb24u.";
        val authenticationToken = AuthenticationToken.of(serializedPayload);

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
}