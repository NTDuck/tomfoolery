package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.auth.AuthenticationToken;
import org.tomfoolery.core.usecases.utils.structs.UserAndRepository;
import org.tomfoolery.core.utils.function.ThrowableConsumer;

import java.time.LocalDateTime;

@RequiredArgsConstructor(staticName = "of")
public class LogUserOutUseCase implements ThrowableConsumer<LogUserOutUseCase.Request> {
    private final @NonNull UserRepositories userRepositories;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenInvalidException {
        val authenticationToken = request.getAuthenticationToken();

        val userAndRepository = getUserAndRepositoryFromAuthenticationToken(authenticationToken);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        markUserAsLoggedOut(user);
        userRepository.save(user);

        invalidateAuthenticationToken(authenticationToken);
    }

    private <User extends ReadonlyUser> UserAndRepository<User> getUserAndRepositoryFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val userId = this.authenticationTokenService.getUserIdFromToken(authenticationToken);
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
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull AuthenticationToken authenticationToken;
    }

    public static class AuthenticationTokenInvalidException extends Exception {}
}
