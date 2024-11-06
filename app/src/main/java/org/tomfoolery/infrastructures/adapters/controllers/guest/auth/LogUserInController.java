package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

public final class LogUserInController implements ThrowableFunctionController<LogUserInController.Request, LogUserInUseCase.Response> {
    private final @NonNull LogUserInUseCase useCase;

    public static @NonNull LogUserInController of(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInController(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInController(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = LogUserInUseCase.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public LogUserInUseCase.@NonNull Response apply(@NonNull Request requestObject) throws LogUserInUseCase.CredentialsInvalidException, LogUserInUseCase.UserNotFoundException, LogUserInUseCase.PasswordMismatchException, LogUserInUseCase.UserAlreadyLoggedInException {
        val requestModel = generateRequestModelFromRequestObject(requestObject);
        val responseModel = this.useCase.apply(requestModel);
        
        return responseModel;
    }

    private static LogUserInUseCase.@NonNull Request<?> generateRequestModelFromRequestObject(@NonNull Request requestObject) {
        val patronCredentials = BaseUser.Credentials.of(requestObject.getUsername(), requestObject.getPassword());
        return LogUserInUseCase.Request.of(patronCredentials);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String username;
        @NonNull String password;
    }
}
