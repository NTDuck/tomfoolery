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

    public static @NonNull UpdateDocumentMetadataController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new UpdateDocumentMetadataController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private UpdateDocumentMetadataController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.updateDocumentMetadataUseCase = UpdateDocumentMetadataUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
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

        byte @NonNull [] rawDocumentCoverImage;

        private UpdateDocumentMetadataUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);
            val documentMetadata = Document.Metadata.of(
                    documentTitle, documentDescription, documentAuthors, documentGenres,
                    Year.of(documentPublishedYear), documentPublisher, Document.Metadata.CoverImage.of(rawDocumentCoverImage)
            );

            return UpdateDocumentMetadataUseCase.Request.of(documentId, documentMetadata);
        }
    }
}
