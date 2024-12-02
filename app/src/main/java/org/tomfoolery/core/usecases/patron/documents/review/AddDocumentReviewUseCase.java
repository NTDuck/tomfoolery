package org.tomfoolery.core.usecases.patron.documents.review;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.signedness.qual.Unsigned;
import org.tomfoolery.core.dataproviders.repositories.relations.ReviewRepository;
import org.tomfoolery.core.dataproviders.generators.users.auth.security.AuthenticationTokenGenerator;
import org.tomfoolery.core.dataproviders.repositories.users.security.AuthenticationTokenRepository;
import org.tomfoolery.core.dataproviders.repositories.documents.DocumentRepository;
import org.tomfoolery.core.domain.relations.Review;
import org.tomfoolery.core.domain.users.Patron;
import org.tomfoolery.core.domain.users.abc.BaseUser;
import org.tomfoolery.core.domain.documents.Document;
import org.tomfoolery.core.usecases.abc.AuthenticatedUserUseCase;
import org.tomfoolery.core.utils.contracts.functional.ThrowableConsumer;

import java.util.Set;

public final class AddDocumentReviewUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<AddDocumentReviewUseCase.Request> {
    private static final @Unsigned int MIN_RATING = 0;
    private static final @Unsigned int MAX_RATING = 5;

    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull ReviewRepository reviewRepository;

    public static @NonNull AddDocumentReviewUseCase of(@NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new AddDocumentReviewUseCase(documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private AddDocumentReviewUseCase(@NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
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
        this.ensureDocumentExists(documentId);

        val reviewId = Review.Id.of(documentId, patronId);
        this.ensureReviewDoesNotExist(reviewId);

        val review = Review.of(reviewId, rating);
        this.reviewRepository.save(review);
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

    private void ensureDocumentExists(Document.@NonNull Id documentId) throws DocumentNotFoundException {
        if (!this.documentRepository.contains(documentId))
            throw new DocumentNotFoundException();
    }

    private void ensureReviewDoesNotExist(Review.@NonNull Id reviewId) throws ReviewAlreadyExistsException {
        if (this.reviewRepository.contains(reviewId))
            throw new ReviewAlreadyExistsException();
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
