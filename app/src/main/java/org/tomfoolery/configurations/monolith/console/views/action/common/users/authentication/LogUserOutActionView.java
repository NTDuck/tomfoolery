package org.tomfoolery.configurations.monolith.console.views.action.common.users.authentication;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.external.common.users.authentication.LogUserOutController;

public final class LogUserOutActionView extends BaseActionView {
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
        loggedInUsername = null;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "User logged out");
    }

    private void onException() {
        this.ioProvider.writeLine(Message.Format.ERROR, "Something went wrong, but now you're a Guest anyway");
    }
}
