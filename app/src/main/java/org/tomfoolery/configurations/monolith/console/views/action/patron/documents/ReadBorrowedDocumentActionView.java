package org.tomfoolery.configurations.monolith.console.views.action.patron.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.PatronSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.patron.documents.borrow.ReadBorrowedDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.patron.documents.ReadBorrowedDocumentController;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import java.io.IOException;

public final class ReadBorrowedDocumentActionView extends UserActionView {
    private final @NonNull ReadBorrowedDocumentController controller;

    public static @NonNull ReadBorrowedDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new ReadBorrowedDocumentActionView(ioProvider, documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private ReadBorrowedDocumentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

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

        } catch (ReadBorrowedDocumentController.DocumentContentUnavailable | IOException exception) {
            this.onDocumentContentUnavailable();
        }
    }

    private ReadBorrowedDocumentController.@NonNull RequestObject collectRequestObject() {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        return ReadBorrowedDocumentController.RequestObject.of(ISBN);
    }

    private void displayViewModel(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws IOException {
        val documentContentFilePath = viewModel.getDocumentContentFilePath();
        FileManager.open(documentContentFilePath);

        this.ioProvider.writeLine(Message.Format.SUCCESS, "The document should be opened promptly");
    }

    private void onSuccess(ReadBorrowedDocumentController.@NonNull ViewModel viewModel) throws IOException {
        this.nextViewClass = PatronSelectionView.class;
        this.displayViewModel(viewModel);
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

        this.ioProvider.writeLine(Message.Format.ERROR, "Document must be borrowed to be read");
    }

    private void onDocumentContentUnavailable() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open document");
    }
}