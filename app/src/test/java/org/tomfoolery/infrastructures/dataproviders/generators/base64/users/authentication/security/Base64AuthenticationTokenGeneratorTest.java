package org.tomfoolery.infrastructures.dataproviders.generators.base64.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGeneratorTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "authentication" })
public class Base64AuthenticationTokenGeneratorTest extends AuthenticationTokenGeneratorTest {
    @Override
    protected @NonNull AuthenticationTokenGenerator createTestSubject() {
        return Base64AuthenticationTokenGenerator.of();
    }
}