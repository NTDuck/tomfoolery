package org.tomfoolery.infrastructures.adapters.controllers.external.patron.documents.review.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.usecases.external.patron.documents.review.persistence.RemoveDocumentReviewUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

public final class RemoveDocumentReviewController implements ThrowableConsumer<RemoveDocumentReviewController.RequestObject> {
    private final @NonNull RemoveDocumentReviewUseCase removeDocumentReviewUseCase;

    public static @NonNull RemoveDocumentReviewController of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentReviewController(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentReviewController(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        this.removeDocumentReviewUseCase = RemoveDocumentReviewUseCase.of(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    @Override
    public void accept(@NonNull RequestObject requestObject) throws RemoveDocumentReviewUseCase.AuthenticationTokenNotFoundException, RemoveDocumentReviewUseCase.AuthenticationTokenInvalidException, RemoveDocumentReviewUseCase.DocumentISBNInvalidException, RemoveDocumentReviewUseCase.DocumentNotFoundException, RemoveDocumentReviewUseCase.ReviewNotFoundException {
        val requestModel = mapRequestObjectToRequestModel(requestObject);
        this.removeDocumentReviewUseCase.accept(requestModel);
    }

    private static RemoveDocumentReviewUseCase.@NonNull Request mapRequestObjectToRequestModel(@NonNull RequestObject requestObject) {
        return RemoveDocumentReviewUseCase.Request.of(requestObject.getDocumentISBN());
    }

    @Value(staticConstructor = "of")
    public static class RequestObject {
        @NonNull String documentISBN;
    }
}
