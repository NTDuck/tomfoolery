package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.documents.modification.UpdateDocumentContentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.infrastructures.utils.helpers.io.file.FileManager;

import java.io.IOException;

public final class UpdateDocumentContentController implements ThrowableConsumer<UpdateDocumentContentController.RequestObject> {
    private final @NonNull UpdateDocumentContentUseCase updateDocumentContentUseCase;

    public static @NonNull UpdateDocumentContentController of(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new UpdateDocumentContentController(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private UpdateDocumentContentController(@NonNull DocumentRepository documentRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.updateDocumentContentUseCase = UpdateDocumentContentUseCase.of(documentRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws DocumentContentFilePathInvalidException, UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException, UpdateDocumentContentUseCase.AuthenticationTokenInvalidException, UpdateDocumentContentUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updateDocumentContentUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;
        @NonNull String newDocumentContentFilePath;

        private UpdateDocumentContentUseCase.@NonNull Request toRequestModel() throws DocumentContentFilePathInvalidException {
            val rawNewDocumentContent = readDocumentContentFromFilePath(this.newDocumentContentFilePath);

            val documentId = Document.Id.of(ISBN);
            val newDocumentContent = Document.Content.of(rawNewDocumentContent);

            return UpdateDocumentContentUseCase.Request.of(documentId, newDocumentContent);
        }

        private static byte @NonNull [] readDocumentContentFromFilePath(@NonNull String documentContentFilePath) throws DocumentContentFilePathInvalidException {
            try {
                return FileManager.read(documentContentFilePath);
            } catch (IOException exception) {
                throw new DocumentContentFilePathInvalidException();
            }
        }
    }

    public static class DocumentContentFilePathInvalidException extends Exception {}
}
