package org.tomfoolery.infrastructures.dataproviders.repositories.inmemory.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepositoryTest;

@Test(groups = { "unit", "repository", "authentication" })
public class InMemoryAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
    @Override
    public @NonNull AuthenticationTokenRepository createTestSubject() {
        return InMemoryAuthenticationTokenRepository.of();
    }
}