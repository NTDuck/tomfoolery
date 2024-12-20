package org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.review.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.documents.review.persistence.AddDocumentReviewUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

public final class AddDocumentReviewController implements ThrowableConsumer<AddDocumentReviewController.RequestObject> {
    private final @NonNull AddDocumentReviewUseCase addDocumentReviewUseCase;

    public static @NonNull AddDocumentReviewController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentReviewController(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentReviewController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.addDocumentReviewUseCase = AddDocumentReviewUseCase.of(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws AddDocumentReviewUseCase.AuthenticationTokenNotFoundException, AddDocumentReviewUseCase.AuthenticationTokenInvalidException, AddDocumentReviewUseCase.DocumentISBNInvalidException, AddDocumentReviewUseCase.DocumentNotFoundException, AddDocumentReviewUseCase.RatingInvalidException, AddDocumentReviewUseCase.ReviewAlreadyExistsException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.addDocumentReviewUseCase.accept(requestModel);
    }

    private static AddDocumentReviewUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return AddDocumentReviewUseCase.Request.of(requestObject.getDocumentISBN(), requestObject.getRating());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
        @Unsigned double rating;
    }
}
