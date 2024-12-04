package org.tomfoolery.configurations.monolith.console.views.action.guest.users.authentication;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.adapters.presenters.guest.auth.LogUserInPresenter;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.users.authentication.LogUserInByCredentialsUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.users.authentication.LogUserInByCredentialsController;

public final class LogUserInByCredentialsActionView extends BaseActionView {
    private final @NonNull LogUserInByCredentialsController controller;
    private final @NonNull LogUserInPresenter presenter;

    public static @NonNull LogUserInByCredentialsActionView of(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new LogUserInByCredentialsActionView(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private LogUserInByCredentialsActionView(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = LogUserInByCredentialsController.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
        this.presenter = LogUserInPresenter.of();
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);
            val adaptedViewModel = this.presenter.apply(viewModel);
            this.onSuccess(adaptedViewModel);

        } catch (LogUserInByCredentialsUseCase.CredentialsInvalidException exception) {
            this.onException(exception, Message.Hint.USERNAME, Message.Hint.PASSWORD);
        } catch (LogUserInByCredentialsUseCase.UserNotFoundException | LogUserInByCredentialsUseCase.PasswordMismatchException exception) {
            this.onException(exception);
        }
    }

    private LogUserInByCredentialsController.@NonNull RequestObject collectRequestObject() {
        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        return LogUserInByCredentialsController.RequestObject.of(username, password);
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        this.nextViewClass = viewModel.getNextViewClass();

        this.ioProvider.writeLine(Message.Format.SUCCESS, "User logged in");
    }
}
