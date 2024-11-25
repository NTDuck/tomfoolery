package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.UpdateDocumentMetadataController;

import java.util.Arrays;

public final class UpdateDocumentMetadataActionView extends UserActionView {
    private final @NonNull UpdateDocumentMetadataController controller;

    public static @NonNull UpdateDocumentMetadataActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentMetadataActionView(ioProvider, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentMetadataActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioProvider);

        this.controller = UpdateDocumentMetadataController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);
            this.onSuccess();

        } catch (DocumentPublishedYearInvalidException e) {
            this.onDocumentPublishedYearInvalidException();

        } catch (UpdateDocumentMetadataController.DocumentCoverImageFilePathInvalidException e) {
            this.onDocumentCoverImageFilePathInvalidException();

        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateDocumentMetadataUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private UpdateDocumentMetadataController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        val documentTitle = this.ioProvider.readLine(Message.Format.PROMPT, "document title");
        val documentDescription = this.ioProvider.readLine(Message.Format.PROMPT, "document description");
        val rawDocumentAuthors = this.ioProvider.readLine(Message.Format.PROMPT, "document authors (separated by ',')");
        val rawDocumentGenres = this.ioProvider.readLine(Message.Format.PROMPT, "document genres (separated by ','");

        val documentPublishedYear = this.collectDocumentPublishedYear();
        val documentPublisher = this.ioProvider.readLine(Message.Format.PROMPT, "document publisher");

        val documentAuthors = Arrays.asList(rawDocumentAuthors.split(","));
        val documentGenres = Arrays.asList(rawDocumentGenres.split(","));

        val documentCoverImageFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document cover image file path");

        return UpdateDocumentMetadataController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher, documentCoverImageFilePath);
    }

    private @Unsigned short collectDocumentPublishedYear() throws DocumentPublishedYearInvalidException {
        val rawDocumentPublishedYear = this.ioProvider.readLine(Message.Format.PROMPT, "document published year");

        try {
            return Short.parseShort(rawDocumentPublishedYear);

        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document metadata updated");
    }

    private void onDocumentPublishedYearInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document published year must be a positive integer");
    }

    private void onDocumentCoverImageFilePathInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Failed to open cover image");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.ERROR, "Document not found");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
