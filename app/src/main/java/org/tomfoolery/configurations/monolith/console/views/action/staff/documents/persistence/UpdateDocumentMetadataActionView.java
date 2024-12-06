package org.tomfoolery.configurations.monolith.console.views.action.staff.documents.persistence;

import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.configurations.monolith.console.dataproviders.providers.io.abc.IOProvider;
import org.tomfoolery.configurations.monolith.console.utils.constants.Message;
import org.tomfoolery.configurations.monolith.console.views.action.abc.UserActionView;
import org.tomfoolery.configurations.monolith.console.views.selection.GuestSelectionView;
import org.tomfoolery.configurations.monolith.console.views.selection.StaffSelectionView;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentMetadataUseCase;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.UpdateDocumentMetadataController;

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

        } catch (UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException | AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (DocumentPublishedYearInvalidException | UpdateDocumentMetadataController.DocumentPublishedYearInvalidException | UpdateDocumentMetadataUseCase.DocumentISBNInvalidException | UpdateDocumentMetadataUseCase.DocumentNotFoundException exception) {
            this.onException(exception);
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

        val documentAuthors = Arrays.asList(Arrays.stream(rawDocumentAuthors.split(",")).parallel().map(String::trim).toArray(String[]::new));
        val documentGenres = Arrays.asList(Arrays.stream(rawDocumentGenres.split(",")).parallel().map(String::trim).toArray(String[]::new));

        return UpdateDocumentMetadataController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher);
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
        this.nextViewClass = StaffSelectionView.class;

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document metadata updated");
    }
    private static class DocumentPublishedYearInvalidException extends Exception {}
}
