package org.tomfoolery.configurations.monolith.terminal.views.action.user.auth;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.user.auth.LogUserOutController;

public final class LogUserOutActionView extends BaseView {
    private final @NonNull LogUserOutController controller;

    public static @NonNull LogUserOutActionView of(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserOutActionView(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserOutActionView(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.nextViewClass = GuestSelectionView.class;
        this.controller = LogUserOutController.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            this.controller.run();
            this.onSuccess();

        } catch (Exception exception) {
            onException();
        }
    }

    private void onSuccess() {
        this.ioProvider.writeLine(Message.Format.SUCCESS, "User logged out");
    }

    private void onException() {
        this.ioProvider.writeLine(Message.Format.ERROR, "Something went wrong, but now you're a Guest anyway");
    }
}
