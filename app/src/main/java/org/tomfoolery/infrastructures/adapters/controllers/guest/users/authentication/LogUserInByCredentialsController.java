package org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.users.authentication.LogUserInByCredentialsUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.users.authentication.security.SecureString;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication.abc.LogUserInController;

public final class LogUserInByCredentialsController extends LogUserInController implements ThrowableFunction<LogUserInByCredentialsController.RequestObject, LogUserInByCredentialsController.ViewModel> {
    private final @NonNull LogUserInByCredentialsUseCase logUserInByCredentialsUseCase;

    public static @NonNull LogUserInByCredentialsController of(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new LogUserInByCredentialsController(userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private LogUserInByCredentialsController(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        this.logUserInByCredentialsUseCase = LogUserInByCredentialsUseCase.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws LogUserInByCredentialsUseCase.CredentialsInvalidException, LogUserInByCredentialsUseCase.UserNotFoundException, LogUserInByCredentialsUseCase.PasswordMismatchException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        val responseModel = this.logUserInByCredentialsUseCase.apply(requestModel);
        val viewModel = mapResponseModelToViewModel(responseModel);

        return viewModel;
    }

    private static LogUserInByCredentialsUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        val password = SecureString.of(requestObject.getPassword());
        val credentials = BaseUser.Credentials.of(requestObject.getUsername(), password);

        return LogUserInByCredentialsUseCase.Request.of(credentials);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        char @NonNull [] password;
    }
}
