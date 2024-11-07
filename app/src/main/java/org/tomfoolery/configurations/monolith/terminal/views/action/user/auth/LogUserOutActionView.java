package org.tomfoolery.configurations.monolith.terminal.views.action.user.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.user.auth.LogUserOutUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;

public final class LogUserOutActionView extends BaseView {
    private final @NonNull LogUserOutUseCase useCase;

    public static @NonNull LogUserOutActionView of(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        return new LogUserOutActionView(ioHandler, authenticationTokenGenerator, authenticationTokenRepository, userRepositories);
    }

    private LogUserOutActionView(@NonNull IOHandler ioHandler, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull UserRepositories userRepositories) {
        super(ioHandler);

        this.nextViewClass = GuestSelectionView.class;
        this.useCase = LogUserOutUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, userRepositories);
    }

    @Override
    public void run() {
        try {
            this.useCase.run();
            onSuccess();

        } catch (Exception exception) {
            onException();
        }
    }

    private void onSuccess() {
        this.ioHandler.writeLine(SUCCESS_MESSAGE_FORMAT, "User logged out");
    }

    private void onException() {
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Something went wrong, but now you're a Guest anyway");
    }
}
