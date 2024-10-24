package org.tomfoolery.configurations.monolith.terminal.views.action.guest.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.ActionView;
import org.tomfoolery.configurations.monolith.terminal.utils.contract.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.utils.services.ScannerService;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.PatronRepository;
import org.tomfoolery.core.dataproviders.auth.PasswordService;
import org.tomfoolery.core.usecases.external.guest.auth.CreatePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.guest.auth.CreatePatronAccountController;

public class CreatePatronAccountActionView implements ActionView {
    private final @NonNull CreatePatronAccountController controller;

    private @NonNull Class<? extends SelectionView> nextViewClass = GuestSelectionView.class;

    private CreatePatronAccountActionView(@NonNull PatronRepository patronRepository, @NonNull PasswordService passwordService) {
        this.controller = CreatePatronAccountController.of(patronRepository, passwordService);
    }

    public static @NonNull CreatePatronAccountActionView of(@NonNull PatronRepository patronRepository, @NonNull PasswordService passwordService) {
        return new CreatePatronAccountActionView(patronRepository, passwordService);
    }

    @Override
    public void run() {
        try {
            val requestObject = getRequestObject();
            this.controller.accept(requestObject);
            onSuccess();
        } catch (PasswordMismatchException exception) {
            onPasswordMismatchException();
        } catch (CreatePatronAccountUseCase.PatronCredentialsInvalidException e) {
            onPatronCredentialsInvalidException();
        } catch (CreatePatronAccountUseCase.PatronAlreadyExistsException e) {
            onPatronAlreadyExistsException();
        }
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return this.nextViewClass;
    }

    private static CreatePatronAccountController.@NonNull RequestObject getRequestObject() throws PasswordMismatchException {
        val scanner = ScannerService.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        System.out.print("Re-enter password: ");
        val passwordAgain = scanner.nextLine();

        if (!password.equals(passwordAgain))
            throw new PasswordMismatchException();

        System.out.print("Enter first name: ");
        val firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        val lastName = scanner.nextLine();

        System.out.print("Enter address: ");
        val address = scanner.nextLine();

        System.out.print("Enter email: ");
        val email = scanner.nextLine();

        return CreatePatronAccountController.RequestObject.of(username, password, firstName, lastName, address, email);
    }

    private void onSuccess() {
        this.nextViewClass = GuestSelectionView.class;
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Password does not match.");
    }

    private void onPatronCredentialsInvalidException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Provided credentials are invalid.");
    }

    private void onPatronAlreadyExistsException() {
        this.nextViewClass = GuestSelectionView.class;
        System.out.println("Error: Patron already exists.");
    }

    private static class PasswordMismatchException extends Exception {}
}
