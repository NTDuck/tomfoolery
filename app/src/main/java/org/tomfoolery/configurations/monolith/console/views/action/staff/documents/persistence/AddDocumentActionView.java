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
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.usecases.staff.documents.persistence.AddDocumentUseCase;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence.AddDocumentController;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.util.Arrays;

public final class AddDocumentActionView extends UserActionView {
    private final @NonNull AddDocumentController addDocumentController;

    public static @NonNull AddDocumentActionView of(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        return new AddDocumentActionView(ioProvider, documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier, fileStorageProvider);
    }

    private AddDocumentActionView(@NonNull IOProvider ioProvider, @NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        super(ioProvider);

        this.addDocumentController = AddDocumentController.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier, fileStorageProvider);
    }

    @Override
    public void run() {
        try {
            val requestObject = this.collectRequestObject();
            this.addDocumentController.accept(requestObject);
            this.onSuccess();

        } catch (AddDocumentUseCase.AuthenticationTokenNotFoundException | AuthenticatedUserUseCase.AuthenticationTokenInvalidException exception) {
            this.onException(exception, GuestSelectionView.class);
        } catch (DocumentPublishedYearInvalidException | AddDocumentController.DocumentPublishedYearInvalidException | AddDocumentController.DocumentContentFilePathInvalidException | AddDocumentController.DocumentCoverImageFilePathInvalidException | AddDocumentUseCase.DocumentISBNInvalidException | AddDocumentUseCase.DocumentAlreadyExistsException | AddDocumentUseCase.DocumentContentInvalidException | AddDocumentUseCase.DocumentCoverImageInvalidException exception) {
            this.onException(exception);
        }
    }

    private AddDocumentController.@NonNull RequestObject collectRequestObject() throws DocumentPublishedYearInvalidException {
        val ISBN = this.ioProvider.readLine(Message.Format.PROMPT, "document ISBN");

        val documentTitle = this.ioProvider.readLine(Message.Format.PROMPT, "document title");
        val documentDescription = this.ioProvider.readLine(Message.Format.PROMPT, "document description");
        val rawDocumentAuthors = this.ioProvider.readLine(Message.Format.PROMPT, "document authors (separated by ',')");
        val rawDocumentGenres = this.ioProvider.readLine(Message.Format.PROMPT, "document genres (separated by ','");

        val documentPublishedYear = this.collectDocumentPublishedYear();
        val documentPublisher = this.ioProvider.readLine(Message.Format.PROMPT, "document publisher");

        val documentAuthors = Arrays.asList(Arrays.stream(rawDocumentAuthors.split(",")).parallel().map(String::trim).toArray(String[]::new));
        val documentGenres = Arrays.asList(Arrays.stream(rawDocumentGenres.split(",")).parallel().map(String::trim).toArray(String[]::new));

        val documentContentFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document file path");
        val documentCoverImageFilePath = this.ioProvider.readLine(Message.Format.PROMPT, "document cover image file path");

        return AddDocumentController.RequestObject.of(ISBN, documentTitle, documentDescription, documentAuthors, documentGenres, documentPublishedYear, documentPublisher, documentContentFilePath, documentCoverImageFilePath);
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

        this.ioProvider.writeLine(Message.Format.SUCCESS, "Document added");
    }

    private static class DocumentPublishedYearInvalidException extends Exception {}
}
