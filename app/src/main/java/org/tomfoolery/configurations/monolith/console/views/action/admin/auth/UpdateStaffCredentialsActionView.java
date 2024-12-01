package org.tomfoolery.configurations.monolith.console.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.admin.users.persistence.UpdateStaffCredentialsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.UpdateStaffCredentialsController;

public final class UpdateStaffCredentialsActionView extends UserActionView {
    private final @NonNull UpdateStaffCredentialsController controller;

    public static @NonNull UpdateStaffCredentialsActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = UpdateStaffCredentialsController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException exception) {
            this.onStaffCredentialsInvalidException();
        } catch (UpdateStaffCredentialsUseCase.StaffNotFoundException exception) {
            this.onStaffNotFoundException();
        }
    }

    private UpdateStaffCredentialsController.@NonNull RequestObject collectRequestObject() {
        val staffId = this.ioProvider.readLine(Message.Format.PROMPT, "staff ID");

        val username = this.ioProvider.readLine(Message.Format.PROMPT, "username");
        val password = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        return UpdateStaffCredentialsController.RequestObject.of(staffId, username, password);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Staff credentials updated");
    }

    private void onStaffCredentialsInvalidException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid username or password");
        this.ioProvider.writeLine("(%s)", Message.USERNAME_CONSTRAINT);
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onStaffNotFoundException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Staff not found");
    }
}
