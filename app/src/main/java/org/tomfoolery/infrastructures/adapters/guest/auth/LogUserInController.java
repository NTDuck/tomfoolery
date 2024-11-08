package org.tomfoolery.infrastructures.adapters.guest.auth;

import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.infrastructures.utils.helpers.reflection.Cloner;

public final class LogUserInController implements ThrowableFunction<LogUserInController.RequestObject, LogUserInUseCase.Response> {
    private final @NonNull LogUserInUseCase logUserInUseCase;

    public static @NonNull LogUserInController of(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInController(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInController(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.logUserInUseCase = LogUserInUseCase.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public LogUserInUseCase.@NonNull Response apply(@NonNull RequestObject requestObject) throws LogUserInUseCase.CredentialsInvalidException, LogUserInUseCase.UserNotFoundException, LogUserInUseCase.PasswordMismatchException, LogUserInUseCase.UserAlreadyLoggedInException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        val responseModel = this.logUserInUseCase.apply(requestModel);

        return responseModel;
    }

    @SneakyThrows
    private static LogUserInUseCase.@NonNull Request generateRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        return Cloner.cloneFrom(requestObject, LogUserInUseCase.Request.class);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        BaseUser.@NonNull Credentials userCredentials;
    }
}
