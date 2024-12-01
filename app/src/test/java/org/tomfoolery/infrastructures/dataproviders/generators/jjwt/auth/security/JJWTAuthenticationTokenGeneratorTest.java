package org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGeneratorTest;

public class JJWTAuthenticationTokenGeneratorTest extends AuthenticationTokenGeneratorTest {
    @Override
    protected @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }
}