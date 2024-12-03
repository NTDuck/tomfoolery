package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepositoryTest;

public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    public @NonNull AuthenticationTokenRepository instantiate() {
        return KeyStoreAuthenticationTokenRepository.of();
    }
}