package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.patron.documents.review.RemoveDocumentReviewUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class RemoveDocumentRatingController implements ThrowableConsumer<RemoveDocumentRatingController.RequestObject> {
    private final @NonNull RemoveDocumentReviewUseCase removeDocumentReviewUseCase;

    public static @NonNull RemoveDocumentRatingController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentRatingController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentRatingController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.removeDocumentReviewUseCase = RemoveDocumentReviewUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws RemoveDocumentReviewUseCase.AuthenticationTokenNotFoundException, RemoveDocumentReviewUseCase.AuthenticationTokenInvalidException, RemoveDocumentReviewUseCase.PatronNotFoundException, RemoveDocumentReviewUseCase.DocumentNotFoundException, RemoveDocumentReviewUseCase.PatronRatingNotFoundException {
        val requestModel = requestObject.toRequestModel();
        this.removeDocumentReviewUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;

        private RemoveDocumentReviewUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return RemoveDocumentReviewUseCase.Request.of(documentId);
        }
    }
}
