package org.tomfoolery.infrastructures.dataproviders.generators.jjwt.auth.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGeneratorTest;

import static org.testng.Assert.*;

public class JJWTAuthenticationTokenGeneratorTest extends AuthenticationTokenGeneratorTest {
    @Override
    protected @NonNull AuthenticationTokenGenerator getAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }
}