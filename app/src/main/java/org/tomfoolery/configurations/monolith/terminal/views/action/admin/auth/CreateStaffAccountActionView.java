package org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.views.abc.ActionView;
import org.tomfoolery.configurations.monolith.terminal.views.abc.SelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.usecases.admin.auth.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.CreateStaffAccountController;

public class CreateStaffAccountActionView implements ActionView {
    private final @NonNull CreateStaffAccountController controller;

    private CreateStaffAccountActionView(@NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.controller = CreateStaffAccountController.of(staffRepository, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    public static @NonNull CreateStaffAccountActionView of(@NonNull StaffRepository staffRepository, @NonNull PasswordEncoder passwordEncoder, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new CreateStaffAccountActionView(staffRepository, passwordEncoder, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = getRequestObject();

        try {
            this.controller.accept(requestObject);
            onSuccess();
        } catch (CreateStaffAccountUseCase.AdminAuthenticationTokenInvalidException exception) {
            onAdminAuthenticationTokenNotFoundException();
        } catch (CreateStaffAccountUseCase.AdminAuthenticationTokenNotFoundException exception) {
            onAdminAuthenticationTokenInvalidException();
        } catch (CreateStaffAccountUseCase.StaffCredentialsInvalidException exception) {
            onStaffCredentialsInvalidException();
        } catch (CreateStaffAccountUseCase.StaffAlreadyExistsException exception) {
            onStaffAlreadyExistsException();
        }
    }

    @Override
    public @NonNull Class<? extends SelectionView> getNextViewClass() {
        return AdministratorSelectionView.class;
    }

    private static CreateStaffAccountController.@NonNull RequestObject getRequestObject() {
        val scanner = ScannerManager.getScanner();

        System.out.print("Enter username: ");
        val username = scanner.nextLine();

        System.out.print("Enter password: ");
        val password = scanner.nextLine();

        return CreateStaffAccountController.RequestObject.of(username, password);
    }

    private static void onSuccess() {
        System.out.println("Success: Patron account created.");
    }

    private static void onAdminAuthenticationTokenInvalidException() {
        System.out.println("Error: Authentication token is invalid.");
    }

    private static void onAdminAuthenticationTokenNotFoundException() {
        System.out.println("Error: Authentication token not found.");
    }

    private static void onStaffCredentialsInvalidException() {
        System.out.println("Error: Provided credentials are invalid.");
    }

    private static void onStaffAlreadyExistsException() {
        System.out.println("Error: Staff already exists.");
    }
}
