package org.tomfoolery.configurations.monolith.console.views.action.administrator.users.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.AdministratorSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.StaffRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.administrator.users.persistence.UpdateStaffCredentialsUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.administrator.users.persistence.UpdateStaffCredentialsController;
import org.tomfoolery.infrastructures.utils.helpers.adapters.UserIdBiAdapter;

public final class UpdateStaffCredentialsActionView extends UserActionView {
    private final @NonNull UpdateStaffCredentialsController updateStaffCredentialsController;

    public static @NonNull UpdateStaffCredentialsActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdateStaffCredentialsActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdateStaffCredentialsActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.updateStaffCredentialsController = UpdateStaffCredentialsController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.updateStaffCredentialsController.accept(requestObject);
            this.onSuccess();

        } catch (UpdateStaffCredentialsUseCase.AuthenticationTokenNotFoundException | UpdateStaffCredentialsUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UserIdBiAdapter.UserUuidInvalidException | UpdateStaffCredentialsUseCase.StaffCredentialsInvalidException | UpdateStaffCredentialsUseCase.StaffNotFoundException exception) {
            this.onException(exception);
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
}
