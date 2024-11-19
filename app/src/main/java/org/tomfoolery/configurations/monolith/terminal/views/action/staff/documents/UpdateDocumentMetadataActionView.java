package org.tomfoolery.configurations.monolith.terminal.views.action.staff.documents;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.configurations.monolith.terminal.dataproviders.generators.io.abc.IOHandler;
import org.tomfoolery.configurations.monolith.terminal.utils.constants.Message;
import org.tomfoolery.configurations.monolith.terminal.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.PatronSelectionView;
import org.tomfoolery.configurations.monolith.terminal.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentMetadataUseCase;
import org.tomfoolery.core.utils.helpers.adapters.Codec;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.UpdateDocumentMetadataController;

import java.util.Arrays;

public final class UpdateDocumentMetadataActionView extends UserActionView {
    private final @NonNull UpdateDocumentMetadataController controller;

    public static @NonNull UpdateDocumentMetadataActionView of(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentMetadataActionView(ioHandler, documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentMetadataActionView(@NonNull IOHandler ioHandler, @NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(ioHandler);

        this.controller = UpdateDocumentMetadataController.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.controller.accept(requestObject);

        } catch (DocumentPublishedYearInvalidException e) {
            this.onDocumentPublishedYearInvalidException();

        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException exception) {
            this.onAuthenticationTokenNotFoundException();
        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException exception) {
            this.onAuthenticationTokenInvalidException();
        } catch (UpdateDocumentMetadataUseCase.DocumentNotFoundException exception) {
            this.onDocumentNotFoundException();
        }
    }

    private UpdateDocumentMetadataController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException {
        val ISBN = this.ioHandler.readLine(Message.Format.PROMPT, "document ISBN");

        val documentTitle = this.ioHandler.readLine(Message.Format.PROMPT, "document title");
        val documentDescription = this.ioHandler.readLine(Message.Format.PROMPT, "document description");
        val rawDocumentAuthors = this.ioHandler.readLine(Message.Format.PROMPT, "document authors (separated by ',')");
        val rawDocumentGenres = this.ioHandler.readLine(Message.Format.PROMPT, "document genres (separated by ','");

        val rawDocumentPublishedYear = this.ioHandler.readLine(Message.Format.PROMPT, "document published year");
        val documentPublisher = this.ioHandler.readLine(Message.Format.PROMPT, "document publisher");

        val rawDocumentCoverImage = this.ioHandler.readLine(Message.Format.PROMPT, "document cover image");

        val documentAuthors = Arrays.asList(rawDocumentAuthors.split(","));
        val documentGenres = Arrays.asList(rawDocumentGenres.split(","));

        val documentCoverImage = Codec.bytesFromChars(Codec.charsFromCharSequence(rawDocumentCoverImage));

        try {
            val documentPublishedYear = Short.parseShort(rawDocumentPublishedYear);
            return UpdateDocumentMetadataController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher, documentCoverImage);

        } catch (NumberFormatException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private void onSuccess() {
        this.nextViewClass = PatronSelectionView.class;

        this.ioHandler.writeLine(Message.Format.SUCCESS, "Document metadata updated");
    }

    private void onDocumentPublishedYearInvalidException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document published year must be a positive integer");
    }

    private void onDocumentNotFoundException() {
        this.nextViewClass = StaffSelectionView.class;

        this.ioHandler.writeLine(Message.Format.ERROR, "Document not found");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
