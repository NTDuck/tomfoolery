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
import org.tomfoolery.core.usecases.external.administrator.users.persistence.CreateStaffAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.administrator.users.persistence.CreateStaffAccountController;

public final class CreateStaffAccountActionView extends UserActionView {
    private final @NonNull CreateStaffAccountController createStaffAccountController;

    public static @NonNull CreateStaffAccountActionView of(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new CreateStaffAccountActionView(ioProvider, staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private CreateStaffAccountActionView(@NonNull IOProvider ioProvider, @NonNull StaffRepository staffRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.createStaffAccountController = CreateStaffAccountController.of(staffRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = collectRequestObject();

        try {
            this.createStaffAccountController.accept(requestObject);
            this.onSuccess();

        } catch (CreateStaffAccountUseCase.AuthenticationTokenNotFoundException | CreateStaffAccountUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (CreateStaffAccountUseCase.StaffCredentialsInvalidException exception) {
            this.onException(exception, Message.Hint.USERNAME, Message.Hint.PASSWORD);
        } catch (CreateStaffAccountUseCase.StaffAlreadyExistsException exception) {
            this.onException(exception);
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
}
