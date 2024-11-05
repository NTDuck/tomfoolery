package org.tomfoolery.infrastructures.dataproviders.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security.KeyStoreAuthenticationTokenRepository;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository() {
        return KeyStoreAuthenticationTokenRepository.of();
    }
}