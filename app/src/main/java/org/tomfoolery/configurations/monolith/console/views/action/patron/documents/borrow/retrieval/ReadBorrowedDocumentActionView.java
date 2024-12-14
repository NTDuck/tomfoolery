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
import org.tomfoolery.core.usecases.external.patron.documents.borrow.retrieval.ReadBorrowedDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.borrow.retrieval.ReadBorrowedDocumentController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

import java.io.IOException;

public final class ReadBorrowedDocumentActionView extends UserActionView {
    private final @NonNull ReadBorrowedDocumentController readBorrowedDocumentController;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull ReadBorrowedDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        return new ReadBorrowedDocumentActionView(ioProvider, hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
    }

    private ReadBorrowedDocumentActionView(@NonNull IOProvider ioProvider, @NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull BorrowingSessionRepository borrowingSessionRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.readBorrowedDocumentController = ReadBorrowedDocumentController.of(hybridDocumentRepository, documentContentRepository, borrowingSessionRepository, authenticationTokenGenerator, authenticationTokenRepository, fileStorageProvider);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void run() {
        val requestObject = this.collectRequestObject();

        try {
            val viewModel = this.readBorrowedDocumentController.apply(requestObject);
            this.onSuccess(viewModel);

        } catch (ReadBorrowedDocumentUseCase.AuthenticationTokenNotFoundException | ReadBorrowedDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (ReadBorrowedDocumentUseCase.DocumentISBNInvalidException | ReadBorrowedDocumentUseCase.DocumentNotFoundException | ReadBorrowedDocumentUseCase.DocumentNotBorrowedException | ReadBorrowedDocumentUseCase.DocumentOverdueException | ReadBorrowedDocumentUseCase.DocumentContentNotFoundException | ReadBorrowedDocumentController.DocumentContentFileWriteException | DocumentContentFileReadException exception) {
            this.onException(exception);
        }
    }

    private ReadBorrowedDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return ReadBorrowedDocumentController.RequestObject.of(ISBN);
    }

    private void displayViewModel(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws DocumentContentFileReadException {
        val documentContentFilePath = viewModel.getDocumentContentFilePath();

        try {
            this.fileStorageProvider.open(documentContentFilePath);
        } catch (IOException exception) {
            throw new DocumentContentFileReadException();
        }

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The document should be opened promptly");
    }

    private void onSuccess(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws DocumentContentFileReadException {
        this.nextViewClass = PatronSelectionView.class;

        this.displayViewModel(viewModel);
    }

    public static class DocumentContentFileReadException extends Exception {}
}
