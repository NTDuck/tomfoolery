package org.tomfoolery.infrastructures.dataproviders.generators.jjwt.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGeneratorTest;

@Test(groups = { "unit", "generator", "authentication" })
public class JJWTAuthenticationTokenGeneratorTest extends AuthenticationTokenGeneratorTest {
    @Override
    protected @NonNull AuthenticationTokenGenerator createTestSubject() {
        return JJWTAuthenticationTokenGenerator.of();
    }
}