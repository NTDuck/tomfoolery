package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.auth.security.PasswordEncoder;
import org.tomfoolery.core.domain.auth.abc.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

public class LogUserInController implements ThrowableFunctionController<LogUserInController.RequestObject, LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private final @NonNull LogUserInUseCase useCase;

    private LogUserInController(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.useCase = LogUserInUseCase.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull LogUserInController of(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInController(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public LogUserInUseCase.@NonNull Request<?> getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val username = requestObject.getUsername();
        val password = requestObject.getPassword();

        val credentials = ReadonlyUser.Credentials.of(username, password);

        return LogUserInUseCase.Request.of(credentials);
    }

    @Override
    public LogUserInUseCase.@NonNull Response apply(@NonNull RequestObject requestObject) throws LogUserInUseCase.CredentialsInvalidException, LogUserInUseCase.UserNotFoundException, LogUserInUseCase.PasswordMismatchException, LogUserInUseCase.UserAlreadyLoggedInException {
        val requestModel = this.getRequestModelFromRequestObject(requestObject);
        val responseModel = this.useCase.apply(requestModel);
        return responseModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;
    }
}
