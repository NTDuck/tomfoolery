package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

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
    public void accept(@NonNull RequestObject requestObject) throws UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException, UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException, UpdateDocumentMetadataUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updateDocumentMetadataUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        @NonNull String documentTitle;
        @NonNull String documentDescription;
        @NonNull List<String> documentAuthors;
        @NonNull List<String> documentGenres;

        @Unsigned short documentPublishedYear;
        @NonNull String documentPublisher;

        byte @NonNull [] documentCoverImage;

        private UpdateDocumentMetadataUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);
            val documentMetadata = Document.Metadata.of(
                documentTitle, documentDescription, documentAuthors, documentGenres,
                Year.of(documentPublishedYear), documentPublisher, Document.Metadata.CoverImage.of(documentCoverImage)
            );

            return UpdateDocumentMetadataUseCase.Request.of(documentId, documentMetadata);
        }
    }
}
