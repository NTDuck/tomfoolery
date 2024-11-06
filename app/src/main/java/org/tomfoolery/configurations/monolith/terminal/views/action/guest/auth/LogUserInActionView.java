package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.user.abc.SharedActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.abc.BaseSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.tomfoolery.infrastructures.adapters.presenters.guest.auth.LogUserInPresenter;

public final class LogUserInActionView extends SharedActionView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;

    private @NonNull Class<? extends BaseSelectionView> nextViewClass = GuestSelectionView.class;

    public static @NonNull LogUserInActionView of(@NonNull IOHandler ioHandler, @NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInActionView(ioHandler, userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInActionView(@NonNull IOHandler ioHandler, @NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler, authenticationTokenGenerator, authenticationTokenRepository);

        this.controller = LogUserInController.of(userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
        this.presenter = LogUserInPresenter.of(authenticationTokenGenerator);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            val viewModel = this.presenter.apply(responseModel);

            onSuccess(viewModel);

        } catch (LogUserInUseCase.CredentialsInvalidException exception) {
            onCredentialsInvalidException();
        } catch (LogUserInUseCase.UserNotFoundException exception) {
            onUserNotFoundException();
        } catch (LogUserInUseCase.PasswordMismatchException exception) {
            onPasswordMismatchException();
        } catch (LogUserInUseCase.UserAlreadyLoggedInException exception) {
            onUserAlreadyLoggedInException();
        }
    }

    @Override
    public @NonNull Class<? extends BaseSelectionView> getNextViewClass() {
        return this.nextViewClass;
    }

    private LogUserInController.@NonNull Request collectRequestObject() {
        val username = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "username");
        val password = this.ioHandler.readPassword(PROMPT_MESSAGE_FORMAT, "password");

        return LogUserInController.Request.of(username, new String(password));
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        val userClass = viewModel.getUserClass();
        this.nextViewClass = getSelectionViewClassFromUserClass(userClass);

        this.ioHandler.writeLine(SUCCESS_MESSAGE_FORMAT, "User logged in");
    }

    private void onCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Invalid username or password");
        this.ioHandler.writeLine("(%s)", USERNAME_CONSTRAINT_MESSAGE);
        this.ioHandler.writeLine("(%s)", PASSWORD_CONSTRAINT_MESSAGE);
    }

    private void onUserNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Username not found");
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Wrong password");
    }

    private void onUserAlreadyLoggedInException() {
        this.nextViewClass = GuestSelectionView.class;
        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "User already logged in on another device");
    }
}
