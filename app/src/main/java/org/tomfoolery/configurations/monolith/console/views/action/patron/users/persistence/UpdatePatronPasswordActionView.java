package org.tomfoolery.configurations.monolith.console.views.action.patron.users.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.external.patron.users.persistence.UpdatePatronPasswordUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence.UpdatePatronPasswordController;

public final class UpdatePatronPasswordActionView extends UserActionView {
    private final @NonNull UpdatePatronPasswordController controller;

    public static @NonNull UpdatePatronPasswordActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new UpdatePatronPasswordActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private UpdatePatronPasswordActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = UpdatePatronPasswordController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdatePatronPasswordUseCase.AuthenticationTokenNotFoundException | AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (UpdatePatronPasswordUseCase.PatronNotFoundException | UpdatePatronPasswordUseCase.PasswordInvalidException | UpdatePatronPasswordUseCase.PasswordMismatchException exception) {
            this.onException(exception);
        }
    }

    private UpdatePatronPasswordController.@NonNull RequestObject collectRequestObject() {
        val oldPatronPassword = this.ioProvider.readPassword(Message.Format.PROMPT, "current password");
        val newPatronPassword = this.ioProvider.readPassword(Message.Format.PROMPT, "new password");

        return UpdatePatronPasswordController.RequestObject.of(oldPatronPassword, newPatronPassword);
    }

    private void onSuccess() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron password updated, please log in again");
    }
}
