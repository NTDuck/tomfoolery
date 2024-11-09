package org.tomfoolery.infrastructures.adapters.controllers.user.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.user.auth.LogUserOutUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.utils.contracts.functional.ThrowableRunnable;

public final class LogUserOutController implements ThrowableRunnable {
    private final @NonNull LogUserOutUseCase logUserOutUseCase;

    public static @NonNull LogUserOutController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        return new LogUserOutController(authenticationTokenGenerator, authenticationTokenRepository, userRepositories);
    }

    private LogUserOutController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        this.logUserOutUseCase = LogUserOutUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, userRepositories);
    }

    @Override
    public void run() throws LogUserOutUseCase.AuthenticationTokenNotFoundException, LogUserOutUseCase.AuthenticationTokenInvalidException {
        this.logUserOutUseCase.run();
    }
}
