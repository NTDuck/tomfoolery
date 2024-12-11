package org.tomfoolery.infrastructures.adapters.controllers.external.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.staff.documents.persistence.UpdateDocumentMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.time.DateTimeException;
import java.time.Year;
import java.util.List;

public final class UpdateDocumentMetadataController implements ThrowableConsumer<UpdateDocumentMetadataController.RequestObject> {
    private final @NonNull UpdateDocumentMetadataUseCase updateDocumentMetadataUseCase;

    public static @NonNull UpdateDocumentMetadataController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentMetadataController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentMetadataController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.updateDocumentMetadataUseCase = UpdateDocumentMetadataUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentPublishedYearInvalidException, UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException, UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException, UpdateDocumentMetadataUseCase.DocumentISBNInvalidException, UpdateDocumentMetadataUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updateDocumentMetadataUseCase.accept(requestModel);
    }

    private static UpdateDocumentMetadataUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws DocumentPublishedYearInvalidException {
        val documentPublishedYear = parseDocumentPublishedYear(requestObject.getDocumentPublishedYear());
        val newDocumentMetadata = Document.Metadata.of(requestObject.getDocumentTitle(), requestObject.getDocumentDescription(), requestObject.getDocumentAuthors(), requestObject.getDocumentGenres(), documentPublishedYear, requestObject.getDocumentPublisher());

        return UpdateDocumentMetadataUseCase.Request.of(requestObject.getDocumentISBN(), newDocumentMetadata);
    }

    private static @NonNull Year parseDocumentPublishedYear(@Unsigned short rawDocumentPublishedYear) throws DocumentPublishedYearInvalidException {
        try {
            return Year.of(rawDocumentPublishedYear);
        } catch (DateTimeException exception) {
            throw new DocumentPublishedYearInvalidException();
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
    }

    public static class DocumentPublishedYearInvalidException extends Exception {}
}
