package org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.admin.auth.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.CreateStaffAccountController;

public final class CreateStaffAccountActionView extends UserActionView {
    private final @NonNull CreateStaffAccountController controller;

    public static @NonNull CreateStaffAccountActionView of(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountActionView(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private CreateStaffAccountActionView(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioHandler);

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
        val username = this.ioHandler.readLine(Message.Format.PROMPT, "username");
        val password = this.ioHandler.readPassword(Message.Format.PROMPT, "password");

        return CreateStaffAccountController.RequestObject.of(username, password);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Staff account created");
    }

    private void onStaffCredentialsInvalidException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioHandler.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioHandler.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onStaffAlreadyExistsException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Staff already exists");
    }
}
