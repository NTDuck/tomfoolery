package org.tomfoolery.core.usecases.abc;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Administrator;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;

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
        val authenticationToken = this.authenticationTokenRepository.get();

        if (authenticationToken == null)
            throw new AuthenticationTokenNotFoundException();

        return authenticationToken;
    }

    protected void ensureAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenGenerator.verify(authenticationToken))
            throw new AuthenticationTokenInvalidException();

        val allowedUserClasses = this.getAllowedUserClasses();
        val retrievedUserClass = this.authenticationTokenGenerator.getUserClassFromAuthenticationToken(authenticationToken);

        if (!allowedUserClasses.contains(retrievedUserClass))
            throw new AuthenticationTokenInvalidException();
    }

    protected BaseUser.@NonNull Id getUserIdFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val userId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (userId == null)
            throw new AuthenticationTokenInvalidException();

        return userId;
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
