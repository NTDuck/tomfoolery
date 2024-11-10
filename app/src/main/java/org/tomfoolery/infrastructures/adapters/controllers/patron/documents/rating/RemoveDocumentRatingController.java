package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.auth.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.auth.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.rating.RemoveDocumentRatingUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class RemoveDocumentRatingController implements ThrowableConsumer<RemoveDocumentRatingController.RequestObject> {
    private final @NonNull RemoveDocumentRatingUseCase removeDocumentRatingUseCase;

    public static @NonNull RemoveDocumentRatingController of(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        return new RemoveDocumentRatingController(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    private RemoveDocumentRatingController(@NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository, @NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository) {
        this.removeDocumentRatingUseCase = RemoveDocumentRatingUseCase.of(authenticationTokenGenerator, authenticationTokenRepository, documentRepository, patronRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws RemoveDocumentRatingUseCase.AuthenticationTokenNotFoundException, RemoveDocumentRatingUseCase.AuthenticationTokenInvalidException, RemoveDocumentRatingUseCase.PatronNotFoundException, RemoveDocumentRatingUseCase.DocumentNotFoundException, RemoveDocumentRatingUseCase.PatronRatingNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.removeDocumentRatingUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private RemoveDocumentRatingUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return RemoveDocumentRatingUseCase.Request.of(documentId);
        }
    }
}