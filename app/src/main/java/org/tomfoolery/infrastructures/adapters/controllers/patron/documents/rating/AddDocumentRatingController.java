package org.tomfoolery.infrastructures.adapters.controllers.patron.documents.rating;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.PatronRepository;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.documents.review.AddDocumentReviewUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

public final class AddDocumentRatingController implements ThrowableConsumer<AddDocumentRatingController.RequestObject> {
    private final @NonNull AddDocumentReviewUseCase addDocumentReviewUseCase;

    public static @NonNull AddDocumentRatingController of(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentRatingController(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentRatingController(@NonNull DocumentRepository documentRepository, @NonNull PatronRepository patronRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.addDocumentReviewUseCase = AddDocumentReviewUseCase.of(documentRepository, patronRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws AddDocumentReviewUseCase.AuthenticationTokenNotFoundException, AddDocumentReviewUseCase.AuthenticationTokenInvalidException, AddDocumentReviewUseCase.PatronNotFoundException, AddDocumentReviewUseCase.DocumentNotFoundException, AddDocumentReviewUseCase.RatingValueInvalidException, AddDocumentReviewUseCase.PatronRatingAlreadyExistsException {
        val requestModel = requestObject.toRequestModel();
        this.addDocumentReviewUseCase.accept(requestModel);
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String ISBN;
        @Unsigned double rating;

        private AddDocumentReviewUseCase.@NonNull Request toRequestModel() {
            val documentId = Document.Id.of(ISBN);

            return AddDocumentReviewUseCase.Request.of(documentId, rating);
        }
    }
}
