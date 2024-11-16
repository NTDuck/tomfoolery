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

    public static @NonNull RemoveDocumentController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.removeDocumentUseCase = RemoveDocumentUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
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
