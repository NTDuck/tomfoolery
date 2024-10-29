package org.tomfoolery.infrastructures.dataproviders.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepositoryTest;

import static org.testng.Assert.*;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }
}