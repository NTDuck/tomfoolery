package org.tomfoolery.infrastructures.adapters.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;

import java.util.function.Function;

public class LogUserInAdapter implements Function<LogUserInAdapter.RequestObject, LogUserInUseCase.Request<?>> {
    @Override
    public LogUserInUseCase.@NonNull Request<?> apply(@NonNull RequestObject requestObject) {
        val username = requestObject.getUsername();
        val password = requestObject.getPassword();

        val credentials = ReadonlyUser.Credentials.of(username, password);

        return LogUserInUseCase.Request.of(credentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;
    }

    @Value(staticConstructor = "of")
    public static class ViewModel {

    }
}
