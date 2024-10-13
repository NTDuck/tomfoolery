package org.tomfoolery.core.usecases.external.user.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.UserRepositories;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "of")
public class LogUserOutUseCase<User extends ReadonlyUser> implements Consumer<LogUserOutUseCase.Request<User>> {
    private final @NonNull UserRepositories userRepositories;

    @Override
    public void accept(@NonNull Request<User> request) {
        val user = request.getUser();

        markUserAsLoggedOut(user);

        val userRepository = this.userRepositories.getUserRepositoryByUser(user);
        assert userRepository != null;

        userRepository.save(user);
    }

    private static <User extends ReadonlyUser> void markUserAsLoggedOut(@NonNull User user) {
        val audit = user.getAudit();
        audit.setLoggedIn(false);

        val timestamps = audit.getTimestamps();
        timestamps.setLastLogout(LocalDateTime.now());
    }

    @Value(staticConstructor = "of")
    public static class Request<User extends ReadonlyUser> {
        @NonNull User user;
    }
}
