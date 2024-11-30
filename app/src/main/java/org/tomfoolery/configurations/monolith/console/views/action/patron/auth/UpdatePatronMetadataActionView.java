package org.tomfoolery.configurations.monolith.console.views.action.patron.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.users.account.patron.modification.UpdatePatronMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.auth.UpdatePatronMetadataController;

public final class UpdatePatronMetadataActionView extends UserActionView {
    private final @NonNull UpdatePatronMetadataController controller;

    public static @NonNull UpdatePatronMetadataActionView of(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdatePatronMetadataActionView(ioProvider, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdatePatronMetadataActionView(@NonNull IOProvider ioProvider, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = UpdatePatronMetadataController.of(patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (UpdatePatronMetadataUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdatePatronMetadataUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdatePatronMetadataUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        }
    }

    private UpdatePatronMetadataController.@NonNull RequestObject collectRequestObject() {
        val patronFirstName = this.ioProvider.readLine(Message.Format.PROMPT, "first name");
        val patronLastName = this.ioProvider.readLine(Message.Format.PROMPT, "last name");

        val patronAddress = this.ioProvider.readLine(Message.Format.PROMPT, "address");
        val patronEmail = this.ioProvider.readLine(Message.Format.PROMPT, "email");

        return UpdatePatronMetadataController.RequestObject.of(patronFirstName, patronLastName, patronAddress, patronEmail);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Patron metadata updated");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }
}
