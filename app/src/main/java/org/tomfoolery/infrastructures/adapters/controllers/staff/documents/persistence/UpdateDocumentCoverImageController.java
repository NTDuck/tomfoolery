package org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentCoverImageUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

import java.io.IOException;

public final class UpdateDocumentCoverImageController implements ThrowableConsumer<UpdateDocumentCoverImageController.RequestObject> {
    private final @NonNull UpdateDocumentCoverImageUseCase updateDocumentCoverImageUseCase;

    public static @NonNull UpdateDocumentCoverImageController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentCoverImageController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentCoverImageController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.updateDocumentCoverImageUseCase = UpdateDocumentCoverImageUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentCoverImageFilePathInvalidException, UpdateDocumentCoverImageUseCase.AuthenticationTokenNotFoundException, UpdateDocumentCoverImageUseCase.AuthenticationTokenInvalidException, UpdateDocumentCoverImageUseCase.DocumentISBNInvalidException, UpdateDocumentCoverImageUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updateDocumentCoverImageUseCase.accept(requestModel);
    }

    private static UpdateDocumentCoverImageUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws DocumentCoverImageFilePathInvalidException {
        val newDocumentCoverImage = readDocumentCoverImageFromFilePath(requestObject.getNewDocumentContentFilePath());
        return UpdateDocumentCoverImageUseCase.Request.of(requestObject.getDocumentISBN(), Document.CoverImage.of(newDocumentCoverImage));
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
        @NonNull String newDocumentContentFilePath;
    }

    public static class DocumentCoverImageFilePathInvalidException extends Exception {}
}
