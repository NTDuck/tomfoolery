package org.tomfoolery.core.usecases.interactive.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.tomfoolery.core.dataproviders.UserRepository;
import org.tomfoolery.core.domain.ReadonlyUser;

import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "of")
public class LogUserOutUseCase<User extends ReadonlyUser> implements Consumer<LogUserOutUseCase<User>.Request> {
    private final @NonNull UserRepository<User> userRepository;

    @Override
    public void accept(@NonNull Request request) {
        val user = request.getUser();
        val audit = user.getAudit();

        audit.setLoggedIn(false);
        this.userRepository.save(user);
    }

    @Value(staticConstructor = "of")
    public class Request {
        @NonNull User user;
    }
}
