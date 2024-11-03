package org.tomfoolery.core.usecases.user.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.utils.contracts.functional.ThrowableRunnable;
import org.tomfoolery.core.utils.dataclasses.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.UserAndRepository;

import java.time.Instant;

public final class LogUserOutUseCase extends AuthenticatedUserUseCase implements ThrowableRunnable {
    private final @NonNull UserRepositories userRepositories;

    public static @NonNull LogUserOutUseCase of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        return new LogUserOutUseCase(authenticationTokenGenerator, authenticationTokenRepository, userRepositories);
    }

    private LogUserOutUseCase(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        super(authenticationTokenGenerator, authenticationTokenRepository);
        this.userRepositories = userRepositories;
    }

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

    private <User extends BaseUser> UserAndRepository<User> getUserAndRepositoryFromAuthenticationToken(@NonNull AuthenticationToken authenticationToken) throws AuthenticationTokenInvalidException {
        val userId = this.authenticationTokenGenerator.getUserIdFromAuthenticationToken(authenticationToken);

        if (userId == null)
            throw new AuthenticationTokenInvalidException();

        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUserId(userId);

        if (userAndRepository == null)
            throw new AuthenticationTokenInvalidException();

        return userAndRepository;
    }

    private static <User extends BaseUser> void markUserAsLoggedOut(@NonNull User user) {
        val userAudit = user.getAudit();
        val userAuditTimestamps = userAudit.getTimestamps();

        userAudit.setLoggedIn(false);
        userAuditTimestamps.setLastLogout(Instant.now());
    }

    private void invalidateAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationTokenGenerator.invalidateAuthenticationToken(authenticationToken);
        this.authenticationTokenRepository.deleteAuthenticationToken();
    }
}
