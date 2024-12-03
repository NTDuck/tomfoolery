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
import org.tomfoolery.infrastructures.dataproviders.providers.io.file.TemporaryFileProvider;

import java.io.IOException;

public final class UpdateDocumentContentController implements ThrowableConsumer<UpdateDocumentContentController.RequestObject> {
    private final @NonNull UpdateDocumentContentUseCase updateDocumentContentUseCase;

    public static @NonNull UpdateDocumentContentController of(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentContentController(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentContentController(@NonNull DocumentRepository documentRepository, @NonNull DocumentContentRepository documentContentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.updateDocumentContentUseCase = UpdateDocumentContentUseCase.of(documentRepository, documentContentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentContentFilePathInvalidException, UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException, UpdateDocumentContentUseCase.AuthenticationTokenInvalidException, UpdateDocumentContentUseCase.DocumentISBNInvalidException, UpdateDocumentContentUseCase.DocumentNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.updateDocumentContentUseCase.accept(requestModel);
    }

    private static UpdateDocumentContentUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) throws DocumentContentFilePathInvalidException {
        val newDocumentContent = readDocumentContentFromFilePath(requestObject.getNewDocumentContentFilePath());
        return UpdateDocumentContentUseCase.Request.of(requestObject.getDocumentISBN(), newDocumentContent);
    }

    private static byte @NonNull [] readDocumentContentFromFilePath(@NonNull String documentContentFilePath) throws DocumentContentFilePathInvalidException {
        try {
            return TemporaryFileProvider.read(documentContentFilePath);
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
