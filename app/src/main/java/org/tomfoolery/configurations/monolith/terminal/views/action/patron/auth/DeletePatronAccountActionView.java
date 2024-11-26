package org.tomfoolery.configurations.monolith.terminal.views.action.patron.auth;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.generators.auth.security.PasswordEncoder;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.auth.DeletePatronAccountUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.auth.DeletePatronAccountController;

public final class DeletePatronAccountActionView extends UserActionView {
    private final @NonNull DeletePatronAccountController controller;

    public static @NonNull DeletePatronAccountActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        return new DeletePatronAccountActionView(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    private DeletePatronAccountActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull PasswordEncoder passwordEncoder) {
        super(ioProvider);

        this.controller = DeletePatronAccountController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository, passwordEncoder);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DeletePatronAccountUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (DeletePatronAccountUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (DeletePatronAccountUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (DeletePatronAccountUseCase.PasswordInvalidException exception) {
            this.onPasswordInvalidException();
        } catch (DeletePatronAccountUseCase.PasswordMismatchException exception) {
            this.onPasswordMismatchException();
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

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onPasswordInvalidException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Invalid password");
        this.ioProvider.writeLine("(%s)", Message.PASSWORD_CONSTRAINT);
    }

    private void onPasswordMismatchException() {
        this.nextViewClass = GuestSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Wrong password");
    }
}
