package org.tomfoolery.configurations.monolith.console.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.adapters.presenters.guest.auth.LogUserInPresenter;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.BaseActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.auth.LogUserInByCredentialsUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInByCredentialsController;

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
            this.onCredentialsInvalidException();
        } catch (LogUserInByCredentialsUseCase.UserNotFoundException exception) {
            this.onUserNotFoundException();
        } catch (LogUserInByCredentialsUseCase.PasswordMismatchException exception) {
            this.onPasswordMismatchException();
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
