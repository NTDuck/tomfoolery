package org.tomfoolery.configurations.monolith.terminal.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.admin.auth.DeleteStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.DeleteStaffAccountController;

public final class DeleteStaffAccountActionView extends UserActionView {
    private final @NonNull DeleteStaffAccountController controller;

    public static @NonNull DeleteStaffAccountActionView of(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountActionView(ioHandler, staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountActionView(@NonNull IOHandler ioHandler, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = DeleteStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DeleteStaffAccountUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (DeleteStaffAccountUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (DeleteStaffAccountUseCase.StaffNotFoundException exception) {
            this.onStaffNotFoundException();
        }
    }

    private DeleteStaffAccountController.@NonNull RequestObject collectRequestObject() {
        val staffId = this.ioHandler.readLine(Message.Format.PROMPT, "staff ID");

        return DeleteStaffAccountController.RequestObject.of(staffId);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Staff account deleted");
    }

    private void onStaffNotFoundException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Staff not found");
    }
}
