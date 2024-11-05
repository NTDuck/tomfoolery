package org.tomfoolery.infrastructures.dataproviders.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.SecretStoreAuthenticationTokenRepository;

public class SecretStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository() {
        return SecretStoreAuthenticationTokenRepository.of();
    }

    @Override
    @Test(enabled = SecretStoreAuthenticationTokenRepository.PERSIST)
    public void testPersistence() {
        super.testPersistence();
    }
}