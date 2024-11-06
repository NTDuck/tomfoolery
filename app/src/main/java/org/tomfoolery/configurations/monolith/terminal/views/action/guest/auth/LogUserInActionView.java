package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.auth.Administrator;
import org.tomfoolery.core.domain.auth.Patron;
import org.tomfoolery.core.domain.auth.Staff;
import org.tomfoolery.core.domain.auth.abc.BaseUser;
import org.tomfoolery.core.usecases.guest.auth.LogUserInUseCase;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.tomfoolery.infrastructures.adapters.presenters.guest.auth.LogUserInPresenter;

import java.util.Arrays;
import java.util.Map;

public class LogUserInActionView implements ActionView {
    private static final @NonNull Map<Class<? extends BaseUser>, Class<? extends SelectionView>> USER_CLASS_TO_VIEW_CLASS_MAP = Map.of(
        Administrator.class, AdministratorSelectionView.class,
        Patron.class, PatronSelectionView.class,
        Staff.class, StaffSelectionView.class
    );

    private final @NonNull IOHandler ioHandler;

    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;

    private @NonNull Class<? extends SelectionView> nextViewClass = GuestSelectionView.class;

    public static @NonNull LogUserInActionView of(@NonNull IOHandler ioHandler, @NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInActionView(ioHandler, userRepositories, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private LogUserInActionView(@NonNull IOHandler ioHandler, @NonNull UserRepositories userRepositories, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.ioHandler = ioHandler;

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
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return this.nextViewClass;
    }

    private LogUserInController.@NonNull Request collectRequestObject() {
        val username = this.ioHandler.readLine(PROMPT_MESSAGE_FORMAT, "username");
        val password = this.ioHandler.readPassword(PROMPT_MESSAGE_FORMAT, "password");

        return LogUserInController.Request.of(username, new String(password));
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        val userClass = viewModel.getUserClass();
        this.nextViewClass = USER_CLASS_TO_VIEW_CLASS_MAP.get(userClass);

        this.ioHandler.writeLine(SUCCESS_MESSAGE_FORMAT, "User logged in");
    }

    private void onCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioHandler.writeLine(ERROR_MESSAGE_FORMAT, "Invalid username or password");
        this.ioHandler.writeLine("Username must be 8-16 characters long; contains only lowercase letters, digits, and underscores; must not start with a digit or end with an underscore.");
        this.ioHandler.writeLine("Password must be 8-32 characters long; contains only letters, digits, and underscores.");
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
