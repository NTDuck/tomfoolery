package org.tomfoolery.infrastructures.dataproviders.inmemory.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.auth.security.InMemoryAuthenticationTokenRepository;

public class InMemoryAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository() {
        return InMemoryAuthenticationTokenRepository.of();
    }

    @Override
    @Test(enabled = false)
    public void testPersistence() {}
}