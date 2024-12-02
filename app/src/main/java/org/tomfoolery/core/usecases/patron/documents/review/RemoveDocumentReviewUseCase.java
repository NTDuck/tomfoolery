package org.tomfoolery.core.usecases.patron.documents.review;

import lombok.Value;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
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

public final class RemoveDocumentReviewUseCase extends AuthenticatedUserUseCase implements ThrowableConsumer<RemoveDocumentReviewUseCase.Request> {
    private final @NonNull DocumentRepository documentRepository;
    private final @NonNull ReviewRepository reviewRepository;

    public static @NonNull RemoveDocumentReviewUseCase of(@NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        return new RemoveDocumentReviewUseCase(documentRepository, reviewRepository, authenticationTokenGenerator, authenticationTokenRepository);
    }

    private RemoveDocumentReviewUseCase(@NonNull DocumentRepository documentRepository, @NonNull ReviewRepository reviewRepository, @NonNull AuthenticationTokenGenerator authenticationTokenGenerator, @NonNull AuthenticationTokenRepository authenticationTokenRepository) {
        super(authenticationTokenGenerator, authenticationTokenRepository);

        this.documentRepository = documentRepository;
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
        this.ensureDocumentExists(documentId);

        val reviewId = Review.Id.of(documentId, patronId);
        this.ensureReviewExists(reviewId);

        this.reviewRepository.delete(reviewId);
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

    private void ensureReviewExists(Review.@NonNull Id reviewId) throws ReviewNotFoundException {
        if (!this.reviewRepository.contains(reviewId))
            throw new ReviewNotFoundException();
    }

    @Value(staticConstructor = "of")
    public static class Request {
        @NonNull String documentISBN;
    }

    public static class DocumentISBNInvalidException extends Exception {}
    public static class DocumentNotFoundException extends Exception {}
    public static class ReviewNotFoundException extends Exception {}
}
