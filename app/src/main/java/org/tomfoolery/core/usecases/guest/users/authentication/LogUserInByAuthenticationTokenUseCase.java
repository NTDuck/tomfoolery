package org.tomfoolery.core.usecases.guest.users.authentication;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.users.authentication.abc.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.UserAndRepository;

@RequiredArgsConstructor(staticName = "of")
public final class LogUserInByAuthenticationTokenUseCase extends LogUserInUseCase implements ThrowableSupplier<LogUserInByAuthenticationTokenUseCase.Response> {
    private final @NonNull UserRepositories userRepositories;

    private final @NonNull AuthenticationTokenGenerator authenticationTokenGenerator;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public @NotNull Response get() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, UserNotFoundException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureUserAuthenticationTokenIsValid(authenticationToken);

        val userAndRepository = this.getUserAndRepositoryFromAuthenticationToken(authenticationToken);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        this.markUserAsLoggedIn(user);
        userRepository.save(user);

        val username = user.getCredentials().getUsername();
        val userClass = user.getClass();
        return Response.of(username, userClass);
    }

    private @NonNull AuthenticationToken getAuthenticationTokenFromRepository() throws AuthenticationTokenNotFoundException {
        val authenticationToken = this.authenticationTokenRepository.get();

        if (authenticationToken == null)
            throw new AuthenticationTokenNotFoundException();

        return authenticationToken;
    }

    private void ensureUserAuthenticationTokenIsValid(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        if (!this.authenticationTokenGenerator.verify(authenticationToken))
            throw new AuthenticationTokenInvalidException();
    }

    private <User extends BaseUser> @NonNull UserAndRepository<User> getUserAndRepositoryFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException, UserNotFoundException {
        val userId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (userId == null)
            throw new AuthenticationTokenInvalidException();

        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUserId(userId);

        if (userAndRepository == null)
            throw new UserNotFoundException();

        return userAndRepository;
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
