package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableFunction;

import java.util.Map;

public final class LogUserInController implements ThrowableFunction<LogUserInController.RequestObject, LogUserInController.ViewModel> {
    private static final @NonNull Map<Class<? extends BaseUser>, UserType> USER_TYPES_BY_USER_CLASSES = Map.of(
        Administrator.class, UserType.ADMINISTRATOR,
        Patron.class, UserType.PATRON,
        Staff.class, UserType.STAFF
    );

    private final @NonNull LogUserInUseCase logUserInUseCase;

    public static @NonNull LogUserInController of(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInController(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInController(@NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.logUserInUseCase = LogUserInUseCase.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel apply(@NonNull RequestObject requestObject) throws LogUserInUseCase.CredentialsInvalidException, LogUserInUseCase.UserNotFoundException, LogUserInUseCase.PasswordMismatchException, LogUserInUseCase.UserAlreadyLoggedInException {
        val requestModel = requestObject.toRequestModel();
        val responseModel = this.logUserInUseCase.apply(requestModel);
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String username;
        @NonNull String password;

        private LogUserInUseCase.@NonNull Request toRequestModel() {
            val credentials = Staff.Credentials.of(username, password);

            return LogUserInUseCase.Request.of(credentials);
        }
    }

    @Value
    public static class ViewModel {
        @NonNull UserType userType;

        private static @NonNull ViewModel fromResponseModel(LogUserInUseCase.@NonNull Response responseModel) {
            val userClass = responseModel.getLoggedInUserClass();
            val userType = USER_TYPES_BY_USER_CLASSES.get(userClass);

            return new ViewModel(userType);
        }
    }

    public enum UserType {
        ADMINISTRATOR, PATRON, STAFF,
    }
}
