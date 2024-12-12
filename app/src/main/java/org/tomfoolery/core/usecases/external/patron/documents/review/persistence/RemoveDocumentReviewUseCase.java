package org.tomfoolery.core.usecases.external.patron.documents.review.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.generators.users.authentication.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.authentication.security.AuthenticationTokenRepository;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.external.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;
import org.tomfoolery.core.dataproviders.repositories.aggregates.hybrids.documents.HybridDocumentRepository;

import java.util.Set;

public final class RemoveDocumentReviewUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentReviewUseCase.Request> {
    private final @NonNull HybridDocumentRepository hybridDocumentRepository;
    private final @NonNull ReviewRepository reviewRepository;

    public static @NonNull RemoveDocumentReviewUseCase of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentReviewUseCase(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentReviewUseCase(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.hybridDocumentRepository = hybridDocumentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, ReviewNotFoundException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val reviewId = Review.Id.of(documentId, patronId);
        this.ensureReviewExists(reviewId);

        this.reviewRepository.delete(reviewId);

        val documentRating = this.calculateDocumentRating(documentId);
        document.setRating(documentRating);
        this.hybridDocumentRepository.save(document);
    }

    private Document.@NonNull Id getDocumentIdFromISBN(@NonNull String documentISBN) throws DocumentISBNInvalidException {
        val documentId = Document.Id.of(documentISBN);

        if (documentId == null)
            throw new DocumentISBNInvalidException();

        return documentId;
    }

    private @NonNull Document getDocumentById(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        val document = this.hybridDocumentRepository.getById(documentId);

        if (document == null)
            throw new DocumentNotFoundException();

        return document;
    }

    private void ensureReviewExists(Review.@NonNull Id reviewId) throws ReviewNotFoundException {
        if (!this.reviewRepository.contains(reviewId))
            throw new ReviewNotFoundException();
    }

    private Document.@NonNull Rating calculateDocumentRating(Document.@NonNull Id documentId) {
        val averageRating = this.reviewRepository.calculateAverageRating(documentId);
        val numberOfRatings = this.reviewRepository.countNumberOfRatings(documentId);

        return Document.Rating.of(averageRating, numberOfRatings);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class ReviewNotFoundException extends Exception {}
}
