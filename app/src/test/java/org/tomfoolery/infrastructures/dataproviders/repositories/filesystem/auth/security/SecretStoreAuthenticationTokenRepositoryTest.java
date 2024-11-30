package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepositoryTest;

public class SecretStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    public @NonNull AuthenticationTokenRepository instantiate() {
        return SecretStoreAuthenticationTokenRepository.of();
    }
}