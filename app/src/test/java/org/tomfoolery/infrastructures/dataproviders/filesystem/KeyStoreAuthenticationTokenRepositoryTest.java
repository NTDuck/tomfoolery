package org.tomfoolery.infrastructures.dataproviders.filesystem;

import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepositoryTest;

import static org.testng.Assert.*;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    protected AuthenticationTokenRepository getAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }
}