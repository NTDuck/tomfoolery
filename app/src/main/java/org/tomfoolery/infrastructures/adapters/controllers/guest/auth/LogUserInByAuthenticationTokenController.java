package org.tomfoolery.infrastructures.adapters.controllers.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.users.authentication.LogUserInByAuthenticationTokenUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableSupplier;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.abc.LogUserInController;

public final class LogUserInByAuthenticationTokenController extends LogUserInController implements ThrowableSupplier<LogUserInByAuthenticationTokenController.ViewModel> {
    private final @NonNull LogUserInByAuthenticationTokenUseCase logUserInByAuthenticationTokenUseCase;

    public static @NonNull LogUserInByAuthenticationTokenController of(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInByAuthenticationTokenController(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInByAuthenticationTokenController(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.logUserInByAuthenticationTokenUseCase = LogUserInByAuthenticationTokenUseCase.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public @NonNull ViewModel get() throws LogUserInByAuthenticationTokenUseCase.AuthenticationTokenNotFoundException, LogUserInByAuthenticationTokenUseCase.AuthenticationTokenInvalidException, LogUserInByAuthenticationTokenUseCase.UserNotFoundException {
        val responseModel = this.logUserInByAuthenticationTokenUseCase.get();
        val viewModel = ViewModel.fromResponseModel(responseModel);

        return viewModel;
    }
}
