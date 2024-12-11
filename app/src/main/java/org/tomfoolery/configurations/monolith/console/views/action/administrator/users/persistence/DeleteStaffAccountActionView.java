package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.administrator.users.persistence.DeleteStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.persistence.DeleteStaffAccountController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class DeleteStaffAccountActionView extends UserActionView {
    private final @NonNull DeleteStaffAccountController deleteStaffAccountController;

    public static @NonNull DeleteStaffAccountActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new DeleteStaffAccountActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private DeleteStaffAccountActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.deleteStaffAccountController = DeleteStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            this.deleteStaffAccountController.accept(requestObject);
            this.onSuccess();

        } catch (DeleteStaffAccountUseCase.AuthenticationTokenNotFoundException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UserIdBiAdapter.UserUuidInvalidException | DeleteStaffAccountUseCase.AuthenticationTokenInvalidException | DeleteStaffAccountUseCase.StaffNotFoundException exception) {
            this.onException(exception);
        }
    }

    private DeleteStaffAccountController.@NonNull RequestObject collectRequestObject() {
        val uuid = this.ioProvider.readLine(Message.Format.PROMPT, "staff UUID");

        return DeleteStaffAccountController.RequestObject.of(uuid);
    }

    private void onSuccess() {
        this.nextViewClass = AdministratorSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Staff account deleted");
    }
}
