package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentMetadataUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import java.io.IOException;
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
    public void accept(@NonNull RequestObject requestObject) throws DocumentCoverImageFilePathInvalidException, UpdateDocumentMetadataUseCase.AuthenticationTokenNotFoundException, UpdateDocumentMetadataUseCase.AuthenticationTokenInvalidException, UpdateDocumentMetadataUseCase.DocumentNotFoundException {
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

        @NonNull String documentCoverImageFilePath;

        private UpdateDocumentMetadataUseCase.@NonNull Request toRequestModel() throws DocumentCoverImageFilePathInvalidException {
            val rawDocumentCoverImage = readDocumentCoverImageFromFilePath(this.documentCoverImageFilePath);

            val documentId = Document.Id.of(ISBN);
            val documentMetadata = Document.Metadata.of(
                documentTitle, documentDescription, documentAuthors, documentGenres,
                Year.of(documentPublishedYear), documentPublisher, Document.Metadata.CoverImage.of(rawDocumentCoverImage)
            );

            return UpdateDocumentMetadataUseCase.Request.of(documentId, documentMetadata);
        }

        private static byte @NonNull [] readDocumentCoverImageFromFilePath(@NonNull String documentCoverImageFilePath) throws DocumentCoverImageFilePathInvalidException {
            try {
                return FileManager.read(documentCoverImageFilePath);
            } catch (IOException exception) {
                throw new DocumentCoverImageFilePathInvalidException();
            }
        }
    }

    public static class DocumentCoverImageFilePathInvalidException extends Exception {}
}
