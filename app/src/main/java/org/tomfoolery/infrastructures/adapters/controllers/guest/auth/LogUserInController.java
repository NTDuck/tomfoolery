package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.Controller;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInController implements Controller<LogUserInController.RequestObject, LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private final @NonNull LogUserInUseCase useCase;

    @Override
    public LogUserInUseCase.@NonNull Request<?> getRequestModelFromRequestObject(@NonNull RequestObject requestObject) {
        val username = requestObject.getUsername();
        val password = requestObject.getPassword();

        val credentials = ReadonlyUser.Credentials.of(username, password);

        return LogUserInUseCase.Request.of(credentials);
    }

    @Override
    public LogUserInUseCase.@NonNull Response getResponseModelFromRequestObject(@NonNull RequestObject requestObject) throws LogUserInUseCase.CredentialsInvalidException, LogUserInUseCase.UserNotFoundException, LogUserInUseCase.PasswordMismatchException, LogUserInUseCase.UserAlreadyLoggedInException {
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
