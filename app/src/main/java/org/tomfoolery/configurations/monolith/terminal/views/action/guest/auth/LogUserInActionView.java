package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.adapters.presenters.guest.auth.LogUserInPresenter;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.abc.BaseView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;

public final class LogUserInActionView extends BaseView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;

    public static @NonNull LogUserInActionView of(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new LogUserInActionView(ioProvider, userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private LogUserInActionView(@NonNull IOProvider ioProvider, @NonNull UserRepositories userRepositories, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = LogUserInController.of(userRepositories, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
        this.presenter = LogUserInPresenter.of();
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);
            val adaptedViewModel = this.presenter.apply(viewModel);
            this.onSuccess(adaptedViewModel);

        } catch (LogUserInUseCase.CredentialsInvalidException exception) {
            onCredentialsInvalidException();
        } catch (LogUserInUseCase.UserNotFoundException exception) {
            onUserNotFoundException();
        } catch (LogUserInUseCase.PasswordMismatchException exception) {
            onPasswordMismatchException();
        }
    }

    private LogUserInController.@NonNull RequestObject collectRequestObject() {
        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        return LogUserInController.RequestObject.of(username, password);
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        this.nextViewClass = viewModel.getNextViewClass();

        this.ioProvider.writeLine(Message.Format.SUCCESS, "User logged in");
    }

    private void onCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioProvider.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onUserNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "User not found");
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Wrong password");
    }
}
