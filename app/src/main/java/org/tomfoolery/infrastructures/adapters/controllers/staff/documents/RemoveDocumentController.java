package org.tomfoolery.infrastructures.adapters.controllers.staff.documents;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.staff.documents.RemoveDocumentUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class RemoveDocumentController implements ThrowableConsumer<RemoveDocumentController.RequestObject> {
    private final @NonNull RemoveDocumentUseCase removeDocumentUseCase;

    public static @NonNull RemoveDocumentController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new RemoveDocumentController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private RemoveDocumentController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        this.removeDocumentUseCase = RemoveDocumentUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws RemoveDocumentUseCase.AuthenticationTokenNotFoundException, RemoveDocumentUseCase.AuthenticationTokenInvalidException, RemoveDocumentUseCase.DocumentNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.removeDocumentUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private RemoveDocumentUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return RemoveDocumentUseCase.Request.of(documentId);
        }
    }
}
