package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.TemporaryFileHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentContentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.UpdateDocumentContentController;

import java.io.IOException;

public final class UpdateDocumentContentActionView extends UserActionView {
    private final @NonNull UpdateDocumentContentController controller;

    public static @NonNull UpdateDocumentContentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentContentActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentContentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = UpdateDocumentContentController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DocumentContentFilePathInvalidException exception) {
            this.onDocumentContentFilePathInvalidException();

        } catch (UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateDocumentContentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateDocumentContentUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private UpdateDocumentContentController.@NonNull RequestObject collectRequestObject() throws DocumentContentFilePathInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");
        val documentContent = this.collectDocumentContent();

        return UpdateDocumentContentController.RequestObject.of(ISBN, documentContent);
    }

    private byte @NonNull [] collectDocumentContent() throws DocumentContentFilePathInvalidException {
        val documentContentFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document file path");

        try {
            return TemporaryFileHandler.read(documentContentFilePath);

        } catch (IOException exception) {
            throw new DocumentContentFilePathInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document content updated");
    }

    private void onDocumentContentFilePathInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open document");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private static class DocumentContentFilePathInvalidException extends Exception {}
}