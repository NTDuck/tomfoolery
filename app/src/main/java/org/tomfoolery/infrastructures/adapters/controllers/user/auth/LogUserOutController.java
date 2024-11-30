package org.tomfoolery.infrastructures.adapters.controllers.user.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.users.authentication.LogUserOutUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableRunnable;

public final class LogUserOutController implements ThrowableRunnable {
    private final @NonNull LogUserOutUseCase logUserOutUseCase;

    public static @NonNull LogUserOutController of(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserOutController(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserOutController(@NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.logUserOutUseCase = LogUserOutUseCase.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() throws LogUserOutUseCase.AuthenticationTokenNotFoundException, LogUserOutUseCase.AuthenticationTokenInvalidException {
        this.logUserOutUseCase.run();
    }
}
