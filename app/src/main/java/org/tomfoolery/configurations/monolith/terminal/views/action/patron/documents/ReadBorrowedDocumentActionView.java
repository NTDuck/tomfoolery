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
import org.tomfoolery.core.usecases.patron.documents.ReadBorrowedDocumentUseCase;
import org.tomfoolery.core.utils.helpers.adapters.Codec;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ReadBorrowedDocumentController;
import org.tomfoolery.infrastructures.utils.helpers.base64.Base64Codec;

public final class ReadBorrowedDocumentActionView extends UserActionView {
    private final @NonNull ReadBorrowedDocumentController controller;

    public static @NonNull ReadBorrowedDocumentActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentActionView(ioHandler, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = ReadBorrowedDocumentController.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.controller.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (ReadBorrowedDocumentUseCase.PatronNotFoundException exception) {
            this.onPatronNotFoundException();
        } catch (ReadBorrowedDocumentUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        } catch (ReadBorrowedDocumentUseCase.DocumentNotBorrowedException exception) {
            this.onDocumentNotBorrowedException();
        }
    }

    private ReadBorrowedDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        return ReadBorrowedDocumentController.RequestObject.of(ISBN);
    }

    private void displayViewModel(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) {
        val documentContent = viewModel.getDocumentContent();

        this.ioHandler.writeLine("Here is the document content, as Base64-encoded char array:");
        this.ioHandler.writeLine(String.valueOf(Base64Codec.encode(Codec.charSequenceFromChars(Codec.charsFromBytes(documentContent)))));
    }

    private void onSuccess(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) {
        this.nextViewClass = PatronSelectionView.class;
        this.displayViewModel(viewModel);
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

        this.ioHandler.writeLine(Message.Format.ERROR, "Document must be borrowed to be read");
    }
}
