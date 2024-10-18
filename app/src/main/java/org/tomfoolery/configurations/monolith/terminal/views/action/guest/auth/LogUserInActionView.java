package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.auth.AuthenticationTokenService;
import org.tomfoolery.core.domain.Administrator;
import org.tomfoolery.core.domain.Patron;
import org.tomfoolery.core.domain.ReadonlyUser;
import org.tomfoolery.core.domain.Staff;
import org.tomfoolery.core.usecases.external.guest.auth.LogUserInUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.LogUserInThrowableFunctionController;

@RequiredArgsConstructor(staticName = "of")
public class LogUserInActionView implements ActionView {
    private final @NonNull LogUserInThrowableFunctionController controller;
    private final @NonNull AuthenticationTokenService authenticationTokenService;

    private @NonNull Class<? extends SelectionView> nextViewClass;

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            val responseModel = this.controller.apply(requestObject);
            onSuccess(responseModel);
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

    private LogUserInThrowableFunctionController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerService.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        return LogUserInThrowableFunctionController.RequestObject.of(username, password);
    }

    private void onSuccess(LogUserInUseCase.@NonNull Response responseModel) {
        val authenticationToken = responseModel.getAuthenticationToken();
        Class<? extends ReadonlyUser> userClass = this.authenticationTokenService.getUserClassFromToken(authenticationToken);

        // Very ugly!
        if (Administrator.class.equals(userClass)) {
            this.nextViewClass = AdministratorSelectionView.class;
        } else if (Patron.class.equals(userClass)) {
            this.nextViewClass = PatronSelectionView.class;
        } else if (Staff.class.equals(userClass)) {
            this.nextViewClass = StaffSelectionView.class;
        } else {
            throw new RuntimeException();
        }

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
        System.out.println("Error: Password mismatch.");
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
