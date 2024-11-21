package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepositoryTest;

import static org.testng.Assert.*;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }
}