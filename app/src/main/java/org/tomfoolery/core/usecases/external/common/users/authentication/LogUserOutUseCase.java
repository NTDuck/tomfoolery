package org.tomfoolery.core.usecases.external.common.users.authentication;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.utils.contracts.functional.ThrowableRunnable;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.AuthenticationToken;
import org.tomfoolery.core.utils.dataclasses.users.UserAndRepository;

import java.time.Instant;

public final class LogUserOutUseCase extends AuthenticatedUserUseCase implements ThrowableRunnable {
    private final @NonNull UserRepositories userRepositories;

    public static @NonNull LogUserOutUseCase of(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserOutUseCase(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserOutUseCase(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.userRepositories = userRepositories;
    }

    @Override
    public void run() throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException {
        val authenticationToken = this.getAuthenticationTokenFromRepository();
        val userId = this.getUserIdFromAuthenticationToken(authenticationToken);

        val userAndRepository = this.getUserAndRepositoryFromUserId(userId);
        val userRepository = userAndRepository.getUserRepository();
        val user = userAndRepository.getUser();

        this.markUserAsLoggedOut(user);
        userRepository.save(user);

        this.invalidateAuthenticationToken(authenticationToken);
    }

    private <User extends BaseUser> UserAndRepository<User> getUserAndRepositoryFromUserId(User.@NonNull Id userId) throws AuthenticationTokenInvalidException {
        UserAndRepository<User> userAndRepository = this.userRepositories.getUserAndRepositoryByUserId(userId);
        assert userAndRepository != null;

        return userAndRepository;
    }

    private <User extends BaseUser> void markUserAsLoggedOut(@NonNull User user) {
        val userAuditTimestamps = user.getAudit().getTimestamps();
        userAuditTimestamps.setLastLogout(Instant.now());
    }

    private void invalidateAuthenticationToken(@NonNull AuthenticationToken authenticationToken) {
        this.authenticationTokenGenerator.invalidate(authenticationToken);
        this.authenticationTokenRepository.remove();
    }
}
