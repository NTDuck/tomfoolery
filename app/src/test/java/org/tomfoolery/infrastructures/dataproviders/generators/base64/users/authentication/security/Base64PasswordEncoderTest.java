package org.tomfoolery.infrastructures.dataproviders.generators.base64.users.authentication.security;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.testng.annotations.Test;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoderTest;

import static org.testng.Assert.*;

@Test(groups = { "unit", "generator", "password" })
public class Base64PasswordEncoderTest extends PasswordEncoderTest {
    @Override
    protected @NonNull PasswordEncoder createTestSubject() {
        return Base64PasswordEncoder.of();
    }
}