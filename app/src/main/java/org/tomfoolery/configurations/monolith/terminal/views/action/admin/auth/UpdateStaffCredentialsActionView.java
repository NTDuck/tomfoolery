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
import org.tomfoolery.core.usecases.admin.auth.UpdateStaffCredentialsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.UpdateStaffCredentialsController;

public final class UpdateStaffCredentialsActionView extends UserActionView {
    private final @NonNull UpdateStaffCredentialsController controller;

    public static @NonNull UpdateStaffCredentialsActionView of(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsActionView(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsActionView(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioHandler);

        this.controller = UpdateStaffCredentialsController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException e) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException e) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException e) {
            this.onStaffCredentialsInvalidException();
        } catch (UpdateStaffCredentialsUseCase.StaffNotFoundException e) {
            this.onStaffNotFoundException();
        }
    }

    private UpdateStaffCredentialsController.@NonNull RequestObject collectRequestObject() {
        val staffId = this.ioHandler.readLine(Message.Format.PROMPT, "staff ID");

        val username = this.ioHandler.readLine(Message.Format.PROMPT, "username");
        val password = this.ioHandler.readPassword(Message.Format.PROMPT, "password");

        return UpdateStaffCredentialsController.RequestObject.of(staffId, username, password);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Staff credentials updated");
    }

    private void onStaffCredentialsInvalidException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioHandler.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioHandler.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onStaffNotFoundException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Staff not found");
    }
}
