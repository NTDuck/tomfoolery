package org.tomfoolery.core.usecases.external.user.auth;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "of")
public class LogUserOutUseCase<User extends ReadonlyUser> implements Consumer<LogUserOutUseCase.Request<User>> {
    private final @NonNull UserRepository<User> userRepository;

    @Override
    public void accept(@NonNull Request<User> request) {
        val user = request.getUser();
        val audit = user.getAudit();

        audit.setLoggedIn(false);
        this.userRepository.save(user);
    }

    @Value(staticConstructor = "of")
    public static class Request<User extends ReadonlyUser> {
        @NonNull User user;
    }
}
