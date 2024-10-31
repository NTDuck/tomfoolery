package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.abc.ReadonlyUser;
import org.tomfoolery.core.utils.contracts.functional.ThrowableRunnable;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.UserAndRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class LogUserOutUseCase implements ThrowableRunnable {
    private final @NonNull UserRepositories userRepositories;

    private final @NonNull AuthenticationTokenService authenticationTokenService;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

    @Override
    public void run() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val authenticationToken = getAuthenticationTokenFromRepository();

        val userAndRepository = getUserAndRepositoryFromAuthenticationToken(authenticationToken);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        markUserAsLoggedOut(user);
        userRepository.save(user);

        invalidateAuthenticationToken(authenticationToken);
    }

    private @NonNull AuthenticationToken getAuthenticationTokenFromRepository() throws AuthenticationTokenNotFoundException {
        val authenticationToken = this.authenticationTokenRepository.getToken();

        if (authenticationToken == null)
            throw new AuthenticationTokenNotFoundException();

        return authenticationToken;
    }

    private <User extends ReadonlyUser> UserAndRepository<User> getUserAndRepositoryFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val userId = this.authenticationTokenService.getUserIdFromToken(authenticationToken);

        if (userId == null)
            throw new AuthenticationTokenInvalidException();

        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUserId(userId);

        if (userAndRepository == null)
            throw new AuthenticationTokenInvalidException();

        return userAndRepository;
    }

    private static <User extends ReadonlyUser> void markUserAsLoggedOut(@NonNull User user) {
        val audit = user.getAudit();
        audit.setLoggedIn(false);

        val timestamps = audit.getTimestamps();
        timestamps.setLastLogout(LocalDateTime.now());
    }

    private void invalidateAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationTokenService.invalidateToken(authenticationToken);
        this.authenticationTokenRepository.removeToken();
    }

    public static class AuthenticationTokenNotFoundException extends Exception {}
    public static class AuthenticationTokenInvalidException extends Exception {}
}
