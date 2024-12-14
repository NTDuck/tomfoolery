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
import org.tomfoolery.core.usecases.external.patron.users.persistence.DeletePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.users.persistence.DeletePatronAccountController;

public final class DeletePatronAccountActionView extends UserActionView {
    private final @NonNull DeletePatronAccountController controller;

    public static @NonNull DeletePatronAccountActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private DeletePatronAccountActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = DeletePatronAccountController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DeletePatronAccountUseCase.AuthenticationTokenNotFoundException | DeletePatronAccountUseCase.AuthenticationTokenInvalidException | DeletePatronAccountUseCase.PasswordMismatchException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (DeletePatronAccountUseCase.PatronNotFoundException | DeletePatronAccountUseCase.PasswordInvalidException exception) {
            this.onException(exception);
        }
    }

    private DeletePatronAccountController.@NonNull RequestObject collectRequestObject() {
        val patronPassword = this.ioProvider.readPassword(Message.Format.PROMPT, "password");

        return DeletePatronAccountController.RequestObject.of(patronPassword);
    }

    private void onSuccess() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron account deleted, redirecting back to Guest view");
    }
}
