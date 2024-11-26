package org.tomfoolery.configurations.monolith.console.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.admin.auth.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.CreateStaffAccountController;

public final class CreateStaffAccountActionView extends UserActionView {
    private final @NonNull CreateStaffAccountController controller;

    public static @NonNull CreateStaffAccountActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private CreateStaffAccountActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = CreateStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (CreateStaffAccountUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (CreateStaffAccountUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (CreateStaffAccountUseCase.StaffCredentialsInvalidException exception) {
            this.onStaffCredentialsInvalidException();
        } catch (CreateStaffAccountUseCase.StaffAlreadyExistsException exception) {
            this.onStaffAlreadyExistsException();
        }
    }

    private CreateStaffAccountController.@NonNull RequestObject collectRequestObject() {
        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        return CreateStaffAccountController.RequestObject.of(username, password);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Staff account created");
    }

    private void onStaffCredentialsInvalidException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioProvider.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onStaffAlreadyExistsException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Staff already exists");
    }
}
