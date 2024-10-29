package org.tomfoolery.infrastructures.dataproviders.hash.jjwt;

import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenGeneratorTest;
import org.tomfoolery.infrastructures.dataproviders.jjwt.auth.security.JJWTAuthenticationTokenGenerator;

public class JJWTAuthenticationTokenGeneratorTest extends AuthenticationTokenGeneratorTest {
    @Override
    protected AuthenticationTokenGenerator getAuthenticationTokenGenerator() {
        return JJWTAuthenticationTokenGenerator.of();
    }
}