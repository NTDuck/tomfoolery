package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.utils.contracts.ThrowableFunctionController;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInThrowableFunctionController implements ThrowableFunctionController<LogUserInThrowableFunctionController.RequestObject, LogUserInUseCase.Request<?>, LogUserInUseCase.Response> {
    private final @NonNull LogUserInUseCase useCase;
    private final @NonNull AuthenticationTokenRepository authenticationTokenRepository;

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

        val authenticationToken = responseModel.getAuthenticationToken();
        this.authenticationTokenRepository.saveToken(authenticationToken);

        return responseModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;
    }
}
