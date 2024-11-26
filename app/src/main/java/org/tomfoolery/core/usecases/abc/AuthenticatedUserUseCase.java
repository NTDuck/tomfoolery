package org.tomfoolery.core.usecases.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.auth.security.AuthenticationToken;

import java.util.Set;

public abstract class AuthenticatedUserUseCase {
    protected final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    protected final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    protected AuthenticatedUserUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.authenticationTokenGenerator = authenticationTokenGenerator;
        this.authenticationTokenRepository = authenticationTokenRepository;
    }

    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Administrator.class, Patron.class, Staff.class);
    }

    protected @NonNull AuthenticationToken getAuthenticationTokenFromRepository() throws AuthenticationTokenNotFoundException {
        val authenticationToken = this.authenticationTokenRepository.getAuthenticationToken();

        if (authenticationToken == null)
            throw new AuthenticationTokenNotFoundException();

        return authenticationToken;
    }

    protected void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenGenerator.verifyAuthenticationToken(authenticationToken))
            throw new AuthenticationTokenInvalidException();

        val allowedUserClasses = this.getAllowedUserClasses();
        val retrievedUserClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);

        if (!allowedUserClasses.contains(retrievedUserClass))
            throw new AuthenticationTokenInvalidException();
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
