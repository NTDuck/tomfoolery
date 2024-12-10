package org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.persistence.AddDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.utils.helpers.verifiers.FileVerifier;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.util.List;

public final class AddDocumentController implements ThrowableConsumer<AddDocumentController.RequestObject> {
    private final @NonNull AddDocumentUseCase addDocumentUseCase;

    public static @NonNull AddDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        return new AddDocumentController(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    private AddDocumentController(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier) {
        this.addDocumentUseCase = AddDocumentUseCase.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentPublishedYearInvalidException, DocumentContentFilePathInvalidException, DocumentContentFilePathInvalidException, DocumentCoverImageFilePathInvalidException, AddDocumentUseCase.AuthenticationTokenNotFoundException, AddDocumentUseCase.AuthenticationTokenInvalidException, AddDocumentUseCase.DocumentISBNInvalidException, AddDocumentUseCase.DocumentAlreadyExistsException, AddDocumentUseCase.DocumentCoverImageInvalidException, AddDocumentUseCase.DocumentContentInvalidException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.addDocumentUseCase.accept(requestModel);
    }

    private static AddDocumentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws DocumentPublishedYearInvalidException, DocumentContentFilePathInvalidException, DocumentCoverImageFilePathInvalidException {
        val documentPublishedYear = parseDocumentPublishedYear(requestObject.getDocumentPublishedYear());
        val documentMetadata = Document.Metadata.of(requestObject.getDocumentTitle(), requestObject.getDocumentDescription(), requestObject.getDocumentAuthors(), requestObject.getDocumentGenres(), documentPublishedYear, requestObject.getDocumentPublisher());

        val documentContent = readDocumentContentFromFilePath(requestObject.getDocumentContentFilePath());
        val documentCoverImage = readDocumentCoverImageFromFilePath(requestObject.getDocumentCoverImageFilePath());

        return AddDocumentUseCase.Request.of(requestObject.getDocumentISBN(), Document.CoverImage.of(documentCoverImage), documentContent, documentMetadata);
    }

    private static @NonNull Year parseDocumentPublishedYear(@Unsigned short rawDocumentPublishedYear) throws DocumentPublishedYearInvalidException {
        try {
            return Year.of(rawDocumentPublishedYear);
        } catch (DateTimeException exception) {
            throw new DocumentPublishedYearInvalidException();
        }
    }

    private static byte @NonNull [] readDocumentContentFromFilePath(@NonNull String documentContentFilePath) throws DocumentContentFilePathInvalidException {
        try {
            return TemporaryFileProvider.read(documentContentFilePath);
        } catch (IOException exception) {
            throw new DocumentContentFilePathInvalidException();
        }
    }

    private static byte @NonNull [] readDocumentCoverImageFromFilePath(@NonNull String documentCoverImageFilePath) throws DocumentCoverImageFilePathInvalidException {
        try {
            return TemporaryFileProvider.read(documentCoverImageFilePath);
        } catch (IOException exception) {
            throw new DocumentCoverImageFilePathInvalidException();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;

        @NonNull String documentTitle;
        @NonNull String documentDescription;
        @NonNull List<String> documentAuthors;
        @NonNull List<String> documentGenres;

        @Unsigned short documentPublishedYear;
        @NonNull String documentPublisher;

        @NonNull String documentContentFilePath;
        @NonNull String documentCoverImageFilePath;
    }

    public static class DocumentPublishedYearInvalidException extends Exception {}
    public static class DocumentContentFilePathInvalidException extends Exception {}
    public static class DocumentCoverImageFilePathInvalidException extends Exception {}
}
