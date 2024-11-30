package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.users.Staff;
import org.tomfoolery.core.usecases.users.authentication.LogUserInByCredentialsUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;
import org.tomfoolery.core.utils.dataclasses.auth.security.SecureString;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.abc.LogUserInController;

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
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.logUserInByCredentialsUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        char @NonNull [] password;

        private LogUserInByCredentialsUseCase.@NonNull Request toRequestModel() {
            val password = SecureString.of(this.password);
            val credentials = Staff.Credentials.of(username, password);

            return LogUserInByCredentialsUseCase.Request.of(credentials);
        }
    }
}
