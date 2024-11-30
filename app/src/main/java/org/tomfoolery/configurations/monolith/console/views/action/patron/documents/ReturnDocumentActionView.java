package org.tomfoolery.configurations.monolith.console.views.action.patron.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.documents.borrow.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ReturnDocumentController;

public final class ReturnDocumentActionView extends UserActionView {
    private final @NonNull ReturnDocumentController controller;

    public static @NonNull ReturnDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentActionView(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = ReturnDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            this.controller.accept(requestObject);

        } catch (ReturnDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ReturnDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReturnDocumentUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (ReturnDocumentUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        } catch (ReturnDocumentUseCase.DocumentNotBorrowedException exception) {
            this.onDocumentNotBorrowedException();
        }
    }

    private ReturnDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return ReturnDocumentController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document returned");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onDocumentNotBorrowedException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document must be borrowed to be returned");
    }
}
