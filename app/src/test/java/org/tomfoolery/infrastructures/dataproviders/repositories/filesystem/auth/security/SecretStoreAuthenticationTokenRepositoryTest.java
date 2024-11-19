// package org.tomfoolery.infrastructures.dataproviders.repositories.filesystem.auth.security;
//
// import org.checkerframework.checker.nullness.qual.NonNull;
// import org.testng.annotations.Test;
// import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
// import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepositoryTest;
//
// import static org.testng.Assert.*;
//
// public class SecretStoreAuthenticationTokenRepositoryTest extends AuthenticationTokenRepositoryTest {
//    @Override
//    protected @NonNull AuthenticationTokenRepository getAuthenticationTokenAuthenticationTokenRepository() {
//        return SecretStoreAuthenticationTokenRepository.of();
//    }
//
//    @Override
//    @Test(enabled = false)
//    public void testPersistence() {
//        super.testPersistence();
//    }
// }