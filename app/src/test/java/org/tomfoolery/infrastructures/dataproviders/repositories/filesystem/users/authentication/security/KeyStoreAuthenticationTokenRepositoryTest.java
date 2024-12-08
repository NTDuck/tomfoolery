package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepositoryTest;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.CdimascioDotenvProvider;
import org.tomfoolery.infrastructures.dataproviders.providers.configurations.dotenv.abc.DotenvProvider;

@Test(groups = { "unit", "repository", "authentication" })
public class KeyStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    public @NonNull AuthenticationTokenRepository createTestSubject() {
        DotenvProvider dotenvProvider = CdimascioDotenvProvider.of();
        return KeyStoreAuthenticationTokenRepository.of(dotenvProvider);
    }
}