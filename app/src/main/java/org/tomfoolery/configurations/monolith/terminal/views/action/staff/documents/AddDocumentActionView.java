package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.utils.helpers.io.TemporaryFileHandler;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.AddDocumentUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.AddDocumentController;

import java.io.IOException;
import java.util.Arrays;

public final class AddDocumentActionView extends UserActionView {
    private final @NonNull AddDocumentController controller;

    public static @NonNull AddDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = AddDocumentController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DocumentPublishedYearInvalidException exception) {
            this.onDocumentPublishedYearInvalidException();
        } catch (DocumentContentFilePathInvalidException exception) {
            this.onDocumentContentFilePathInvalidException();
        } catch (DocumentCoverImageFilePathInvalidException exception) {
            this.onDocumentCoverImageFilePathInvalidException();

        } catch (AddDocumentUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (AddDocumentUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (AddDocumentUseCase.DocumentAlreadyExistsException exception) {
            this.onDocumentAlreadyExistsException();
        }
    }

    private AddDocumentController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException, DocumentContentFilePathInvalidException, DocumentCoverImageFilePathInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        val documentTitle = this.ioProvider.readLine(Message.Format.PROMPT, "document title");
        val documentDescription = this.ioProvider.readLine(Message.Format.PROMPT, "document description");
        val rawDocumentAuthors = this.ioProvider.readLine(Message.Format.PROMPT, "document authors (separated by ',')");
        val rawDocumentGenres = this.ioProvider.readLine(Message.Format.PROMPT, "document genres (separated by ','");

        val documentPublishedYear = this.collectDocumentPublishedYear();
        val documentPublisher = this.ioProvider.readLine(Message.Format.PROMPT, "document publisher");

        val documentAuthors = Arrays.asList(rawDocumentAuthors.split(","));
        val documentGenres = Arrays.asList(rawDocumentGenres.split(","));

        val documentContent = this.collectDocumentContent();
        val documentCoverImage = this.collectDocumentCoverImage();

        return AddDocumentController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher, documentContent, documentCoverImage);
    }

    private @Unsigned short collectDocumentPublishedYear() throws DocumentPublishedYearInvalidException {
        val rawDocumentPublishedYear = this.ioProvider.readLine(Message.Format.PROMPT, "document published year");

        try {
            return Short.parseShort(rawDocumentPublishedYear);

        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private byte @NonNull [] collectDocumentContent() throws DocumentContentFilePathInvalidException {
        val documentContentFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document file path");

        try {
            return TemporaryFileHandler.read(documentContentFilePath);

        } catch (IOException exception) {
            throw new DocumentContentFilePathInvalidException();
        }
    }

    private byte @NonNull [] collectDocumentCoverImage() throws DocumentCoverImageFilePathInvalidException {
        val documentCoverImageFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "cover image file path");

        try {
            return TemporaryFileHandler.read(documentCoverImageFilePath);

        } catch (IOException exception) {
            throw new DocumentCoverImageFilePathInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document added");
    }

    private void onDocumentPublishedYearInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document published year must be a positive integer");
    }

    private void onDocumentContentFilePathInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open document");
    }

    private void onDocumentCoverImageFilePathInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open cover image");
    }

    private void onDocumentAlreadyExistsException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document already exists");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
    private static class DocumentContentFilePathInvalidException extends Exception {}
    private static class DocumentCoverImageFilePathInvalidException extends Exception {}
}
