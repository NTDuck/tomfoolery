package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.UpdateDocumentContentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class UpdateDocumentContentController implements ThrowableConsumer<UpdateDocumentContentController.RequestObject> {
    private final @NonNull UpdateDocumentContentUseCase updateDocumentContentUseCase;

    public static @NonNull UpdateDocumentContentController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        return new UpdateDocumentContentController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    private UpdateDocumentContentController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository) {
        this.updateDocumentContentUseCase = UpdateDocumentContentUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws UpdateDocumentContentUseCase.AuthenticationTokenNotFoundException, UpdateDocumentContentUseCase.AuthenticationTokenInvalidException, UpdateDocumentContentUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.updateDocumentContentUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;
        byte @NonNull [] rawNewDocumentContent;

        private UpdateDocumentContentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);
            val newDocumentContent = Document.Content.of(rawNewDocumentContent);

            return UpdateDocumentContentUseCase.Request.of(documentId, newDocumentContent);
        }
    }
}
