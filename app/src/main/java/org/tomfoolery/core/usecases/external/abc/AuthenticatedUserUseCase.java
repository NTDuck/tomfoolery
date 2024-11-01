package org.tomfoolery.core.usecases.external.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;

import java.util.Collection;
import java.util.List;

public abstract class AuthenticatedUserUseCase {
    protected final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    protected final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    protected AuthenticatedUserUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.authenticationTokenGenerator = authenticationTokenGenerator;
        this.authenticationTokenRepository = authenticationTokenRepository;
    }

    protected @NonNull Collection<Class<? extends BaseUser>> getAllowedUserClasses() {
        return List.of();
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

        val allowedUserClasses = getAllowedUserClasses();

        if (allowedUserClasses.isEmpty())
            return;

        val retrievedUserClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);

        if (!allowedUserClasses.contains(retrievedUserClass))
            throw new AuthenticationTokenInvalidException();
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
