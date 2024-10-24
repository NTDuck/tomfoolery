package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.utils.containers.UserRepositories;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInController;
import org.tomfoolery.infrastructures.adapters.presenters.guest.auth.LogUserInPresenter;

public class LogUserInActionView implements ActionView {
    private final @NonNull LogUserInController controller;
    private final @NonNull LogUserInPresenter presenter;

    private @NonNull Class<? extends SelectionView> nextViewClass = GuestSelectionView.class;

    private LogUserInActionView(@NonNull UserRepositories userRepositories, @NonNull PasswordService passwordService, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = LogUserInController.of(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository);
        this.presenter = LogUserInPresenter.of(authenticationTokenService);
    }

    public static @NonNull LogUserInActionView of(@NonNull UserRepositories userRepositories, @NonNull PasswordService passwordService, @NonNull AuthenticationTokenService authenticationTokenService, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new LogUserInActionView(userRepositories, passwordService, authenticationTokenService, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            val viewModel = this.presenter.getViewModelFromResponseModel(responseModel);
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

    private static LogUserInController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerService.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        return LogUserInController.RequestObject.of(username, password);
    }

    private void onSuccess(LogUserInPresenter.@NonNull ViewModel viewModel) {
        this.nextViewClass = viewModel.getNextViewClass();
        System.out.println("Success: User logged in.");
    }

    private void onCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Provided credentials are invalid.");
    }

    private void onUserNotFoundException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Found no user with matching username.");
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Password does not match.");
    }

    private void onUserAlreadyLoggedInException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: User already logged in on another device.");
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return this.nextViewClass;
    }
}
