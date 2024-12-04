package org.tomfoolery.configurations.monolith.console.views.action.patron.documents.borrow.retrieval;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.BorrowingSessionRepository;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.retrieval.ReadBorrowedDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.borrow.retrieval.ReadBorrowedDocumentController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

import java.io.IOException;

public final class ReadBorrowedDocumentActionView extends UserActionView {
    private final @NonNull ReadBorrowedDocumentController readBorrowedDocumentController;

    public static @NonNull ReadBorrowedDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentActionView(ioProvider, documentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.readBorrowedDocumentController = ReadBorrowedDocumentController.of(documentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.readBorrowedDocumentController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException | ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (ReadBorrowedDocumentUseCase.DocumentISBNInvalidException | ReadBorrowedDocumentUseCase.DocumentNotFoundException | ReadBorrowedDocumentUseCase.DocumentNotBorrowedException | ReadBorrowedDocumentUseCase.DocumentOverdueException | ReadBorrowedDocumentUseCase.DocumentContentNotFoundException | ReadBorrowedDocumentController.DocumentContentUnavailable | DocumentContentNotOpenableException exception) {
            this.onException(exception);
        }
    }

    private ReadBorrowedDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return ReadBorrowedDocumentController.RequestObject.of(ISBN);
    }

    private void displayViewModel(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws DocumentContentNotOpenableException {
        val documentContentFilePath = viewModel.getDocumentContentFilePath();

        try {
            TemporaryFileProvider.open(documentContentFilePath);
        } catch (IOException exception) {
            throw new DocumentContentNotOpenableException();
        }

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The document should be opened promptly");
    }

    private void onSuccess(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws DocumentContentNotOpenableException {
        this.nextViewClass = PatronSelectionView.class;

        this.displayViewModel(viewModel);
    }

    public static class DocumentContentNotOpenableException extends Exception {}
}
