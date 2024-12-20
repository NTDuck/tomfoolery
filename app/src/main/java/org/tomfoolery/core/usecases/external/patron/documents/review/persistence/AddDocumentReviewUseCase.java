package org.tomfoolery.core.usecases.external.patron.documents.review.persistence;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
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

public final class AddDocumentReviewUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentReviewUseCase.Request> {
    public static final @Unsigned int MIN_RATING = 0;
    public static final @Unsigned int MAX_RATING = 5;

    private final @NonNull HybridDocumentRepository hybridDocumentRepository;
    private final @NonNull ReviewRepository reviewRepository;

    public static @NonNull AddDocumentReviewUseCase of(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentReviewUseCase(hybridDocumentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentReviewUseCase(@NonNull HybridDocumentRepository hybridDocumentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.hybridDocumentRepository = hybridDocumentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    protected @NonNull Set<Class<? extends BaseUser>> getAllowedUserClasses() {
        return Set.of(Patron.class);
    }

    @Override
    public void accept(@NonNull Request request) throws AuthenticationTokenNotFoundException, AuthenticationTokenInvalidException, DocumentISBNInvalidException, DocumentNotFoundException, RatingInvalidException, ReviewAlreadyExistsException {
        val patronAuthenticationToken = this.getAuthenticationTokenFromRepository();
        this.ensureAuthenticationTokenIsValid(patronAuthenticationToken);
        val patronId = this.getUserIdFromAuthenticationToken(patronAuthenticationToken);

        val rating = request.getRating();
        this.ensureRatingIsValid(rating);

        val documentISBN = request.getDocumentISBN();
        val documentId = this.getDocumentIdFromISBN(documentISBN);
        val document = this.getDocumentById(documentId);

        val reviewId = Review.Id.of(documentId, patronId);
        this.ensureReviewDoesNotExist(reviewId);

        val review = Review.of(reviewId, rating);
        this.reviewRepository.save(review);

        val documentRating = this.calculateDocumentRating(documentId);
        document.setRating(documentRating);
        this.hybridDocumentRepository.save(document);
    }

    private void ensureRatingIsValid(@Unsigned double rating) throws RatingInvalidException {
        if (rating < MIN_RATING || rating > MAX_RATING)
            throw new RatingInvalidException();
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

    private void ensureReviewDoesNotExist(Review.@NonNull Id reviewId) throws ReviewAlreadyExistsException {
        if (this.reviewRepository.contains(reviewId))
            throw new ReviewAlreadyExistsException();
    }

    private Document.@NonNull Rating calculateDocumentRating(Document.@NonNull Id documentId) {
        val averageRating = this.reviewRepository.calculateAverageRating(documentId);
        val numberOfRatings = this.reviewRepository.countNumberOfRatings(documentId);

        return Document.Rating.of(averageRating, numberOfRatings);
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
        @Unsigned double rating;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class RatingInvalidException extends Exception {}
    public static class ReviewAlreadyExistsException extends Exception {}
}
