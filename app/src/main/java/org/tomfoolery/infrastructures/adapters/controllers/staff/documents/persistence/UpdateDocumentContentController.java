package org.tomfoolery.infrastructures.adapters.controllers.staff.documents.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.DocumentContentRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.usecases.staff.documents.persistence.UpdateDocumentContentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.providers.io.file.FileVerifier;
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.abc.FileStorageProvider;

import java.io.IOException;

public final class UpdateDocumentContentController implements ThrowableConsumer<UpdateDocumentContentController.RequestObject> {
    private final @NonNull UpdateDocumentContentUseCase updateDocumentContentUseCase;
    private final @NonNull FileStorageProvider fileStorageProvider;

    public static @NonNull UpdateDocumentContentController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        return new UpdateDocumentContentController(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier, fileStorageProvider);
    }

    private UpdateDocumentContentController(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull FileVerifier fileVerifier, @NonNull FileStorageProvider fileStorageProvider) {
        this.updateDocumentContentUseCase = UpdateDocumentContentUseCase.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository, fileVerifier);
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentContentFilePathInvalidException, UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException, UpdateDocumentContentUseCase.AuthenticationTokenInvalidException, UpdateDocumentContentUseCase.DocumentISBNInvalidException, UpdateDocumentContentUseCase.DocumentNotFoundException, UpdateDocumentContentUseCase.DocumentContentInvalidException {
        val requestModel = this.mapRequestObjectToRequestModel(requestObject);
        this.updateDocumentContentUseCase.accept(requestModel);
    }

    private UpdateDocumentContentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws DocumentContentFilePathInvalidException {
        val newDocumentContent = this.readDocumentContentFromFilePath(requestObject.getNewDocumentContentFilePath());
        return UpdateDocumentContentUseCase.Request.of(requestObject.getDocumentISBN(), newDocumentContent);
    }

    private byte @NonNull [] readDocumentContentFromFilePath(@NonNull String documentContentFilePath) throws DocumentContentFilePathInvalidException {
        try {
            return this.fileStorageProvider.read(documentContentFilePath);
        } catch (IOException exception) {
            throw new DocumentContentFilePathInvalidException();
        }
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
        @NonNull String newDocumentContentFilePath;
    }

    public static class DocumentContentFilePathInvalidException extends Exception {}
}
