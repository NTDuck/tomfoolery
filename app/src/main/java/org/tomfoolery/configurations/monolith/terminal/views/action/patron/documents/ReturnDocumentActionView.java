package org.tomfoolery.configurations.monolith.terminal.views.action.patron.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.ReturnDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ReturnDocumentController;

public final class ReturnDocumentActionView extends UserActionView {
    private final @NonNull ReturnDocumentController controller;

    public static @NonNull ReturnDocumentActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReturnDocumentActionView(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReturnDocumentActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

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
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        return ReturnDocumentController.RequestObject.of(ISBN);
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Document returned");
    }

    private void onPatronNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Patron not found");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }

    private void onDocumentNotBorrowedException() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document must be borrowed to be returned");
    }
}
