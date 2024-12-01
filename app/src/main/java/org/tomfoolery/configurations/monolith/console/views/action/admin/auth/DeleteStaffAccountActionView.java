package org.tomfoolery.configurations.monolith.console.views.action.admin.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.admin.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.admin.auth.DeleteStaffAccountController;

public final class DeleteStaffAccountActionView extends UserActionView {
    private final @NonNull DeleteStaffAccountController controller;

    public static @NonNull DeleteStaffAccountActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

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
        val staffId = this.ioProvider.readLine(Message.Format.PROMPT, "staff ID");

        return DeleteStaffAccountController.RequestObject.of(staffId);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Staff account deleted");
    }

    private void onStaffNotFoundException() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Staff not found");
    }
}
