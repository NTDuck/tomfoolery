package org.tomfoolery.infrastructures.dataproviders.hash.jjwt;

import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenServiceTest;

import static org.testng.Assert.*;

public class JJWTAuthenticationTokenServiceTest extends AuthenticationTokenServiceTest {
    @Override
    protected AuthenticationTokenService getAuthenticationTokenService() {
        return JJWTAuthenticationTokenService.of();
    }
}